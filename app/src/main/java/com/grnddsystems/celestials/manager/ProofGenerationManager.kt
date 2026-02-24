@file:OptIn(ExperimentalStdlibApi::class)

package com.grnddsystems.celestials.manager

import CircuitAlgorithmType
import CircuitPassportHashType
import RegisterIdentityCircuitType
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.noirandroid.lib.Circuit
import com.grnddsystems.celestials.BaseConfig
import com.grnddsystems.celestials.util.NoirLock
import kotlinx.coroutines.sync.withLock
import com.grnddsystems.celestials.BuildConfig
import com.grnddsystems.celestials.api.registration.PassportAlreadyRegisteredByOtherPK
import com.grnddsystems.celestials.data.enums.PassportStatus
import com.grnddsystems.celestials.modules.passportScan.CircuitDownloader
import com.grnddsystems.celestials.modules.passportScan.CircuitNoirDownloader
import com.grnddsystems.celestials.modules.passportScan.DownloadCircuitError
import com.grnddsystems.celestials.modules.passportScan.DownloadRequest
import com.grnddsystems.celestials.modules.passportScan.models.CryptoUtilsPassport
import com.grnddsystems.celestials.modules.passportScan.models.EDocument
import com.grnddsystems.celestials.modules.passportScan.models.RegisterIdentityInputs
import com.grnddsystems.celestials.modules.passportScan.models.RegisterIdentityLightInputs
import com.grnddsystems.celestials.modules.passportScan.nfc.SODFileOwn
import com.grnddsystems.celestials.util.Constants.NOT_ALLOWED_COUNTRIES
import com.grnddsystems.celestials.util.ErrorHandler
import com.grnddsystems.celestials.util.SecurityUtil
import com.grnddsystems.celestials.util.ZKPUseCase
import com.grnddsystems.celestials.util.circuits.CircuitUtil
import com.grnddsystems.celestials.util.circuits.RegisterNoirCircuitData
import com.grnddsystems.celestials.util.circuits.RegisteredCircuitData
import com.grnddsystems.celestials.util.data.GrothProof
import com.grnddsystems.celestials.util.data.UniversalProof
import com.grnddsystems.celestials.util.data.UniversalProofFactory
import com.grnddsystems.celestials.util.decodeHexString
import com.grnddsystems.celestials.util.generateLightRegistrationProofByCircuitType
import com.grnddsystems.celestials.util.generateRegistrationProofByCircuitType
import com.grnddsystems.celestials.util.toBits
import identity.X509Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.web3j.utils.Numeric
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

enum class PassportProofState(val value: Int) {
    READING_DATA(0), APPLYING_ZERO_KNOWLEDGE(1), CREATING_CONFIDENTIAL_PROFILE(2), FINALIZING(3)
}

@Singleton
class ProofGenerationManager @Inject constructor(
    private val application: Context,
    private val identityManager: IdentityManager,
    private val registrationManager: RegistrationManager,
    private val passportManager: PassportManager,
    private val rarimoContractManager: RarimoContractManager,
    private val pointsManager: PointsManager,
) {

    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // State that can be observed by other components (e.g. view models).
    private val _state = MutableStateFlow(PassportProofState.READING_DATA)
    val state: StateFlow<PassportProofState> get() = _state.asStateFlow()
    private val _downloadProgress = MutableStateFlow(0)
    private var currentRegistration: kotlinx.coroutines.Deferred<UniversalProof>? = null

    //Download for circuits
    val downloadProgress: StateFlow<Int> = _downloadProgress.asStateFlow()

    private var _proofError: MutableStateFlow<Exception?> = MutableStateFlow(null)

    val proofError: StateFlow<Exception?> get() = _proofError.asStateFlow()

    private val TAG = ProofGenerationManager::class.java.simpleName
    private val second = 1000L
    // Changed to property to get fresh value each time (key is auto-generated on first launch)
    private val privateKeyBytes get() = identityManager.privateKeyBytes

    private fun resetState() {
        _state.value = PassportProofState.READING_DATA
        _proofError.value = null
    }

    private suspend fun registerCertificate(eDocument: EDocument) {
        try {
            val sodStream = eDocument.sod!!.decodeHexString().inputStream()
            val sodFile = SODFileOwn(sodStream)
            val x509Util = X509Util()

            val slaveCertificate = SecurityUtil.convertToPEM(sodFile.docSigningCertificate)
            val certificatesSMTAddress = BaseConfig.CERTIFICATES_SMT_CONTRACT_ADDRESS
            val certificatesSMTContract =
                rarimoContractManager.getPoseidonSMT(certificatesSMTAddress)
            val icao = readICAO(application.applicationContext)

            val slaveCertificateIndex = x509Util.getSlaveCertificateIndex(
                slaveCertificate.toByteArray(), icao
            )

            val proof = withContext(Dispatchers.IO) {
                certificatesSMTContract.getProof(slaveCertificateIndex).send()
            }
            if (proof?.existence == true) {
                ErrorHandler.logDebug(TAG, "Passport certificate is already registered")
                return
            }
            val callDataBuilder = identity.CallDataBuilder()
            val callData = callDataBuilder.buildRegisterCertificateCalldata(
                icao, slaveCertificate.toByteArray()
            )

            val response = withContext(Dispatchers.IO) {
                registrationManager.relayerRegister(
                    callData.calldata, BaseConfig.REGISTER_CONTRACT_ADDRESS
                )
            }
            ErrorHandler.logDebug(
                TAG, "Passport certificate EVM Tx Hash ${response.data.attributes.tx_hash}"
            )

            val res =
                rarimoContractManager.checkIsTransactionSuccessful(response.data.attributes.tx_hash)
            if (!res) {
                ErrorHandler.logError(TAG, "Transaction failed ${response.data.attributes.tx_hash}")
            }
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error in registerCertificate", e)
            throw e
        }
    }

    private suspend fun registerByDocument(eDocument: EDocument): UniversalProof {
        try {
            _state.value = PassportProofState.READING_DATA
            val circuitType = getCircuitType(eDocument)
            registrationManager.setCircuitData(circuitType)
            val circuitName = getCircuitName(circuitType)

            _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

            val proof = if (RegisterNoirCircuitData.fromValue(circuitType.buildName()) != null) {
                Log.d("Before registration", circuitType.buildName().toString())
                generateRegisterIdentityProofPlonk(
                    eDocument, registerIdentityCircuitType = circuitType
                )
            } else {
                 Log.d("Before registration", "Circom proof generation")
                generateRegisterIdentityProofGroth(eDocument, circuitType)
            }


            if (!BuildConfig.isTestnet) {
                try {
                    ErrorHandler.logDebug(TAG, "Deleting redundant circuit files")
                    //circuitDownloader.deleteRedunantFiles(circuitData)
                } catch (e: Exception) {
                    ErrorHandler.logError(TAG, "Error deleting redundant circuit files", e)
                }
            }

            registrationManager.setRegistrationProof(proof)
            _state.value = PassportProofState.CREATING_CONFIDENTIAL_PROFILE

            var doc = eDocument.copy()
            val isDocumentRegistered = try {
                isDocumentRegistered(eDocument, proof)
            } catch (e: Exception) {
                if (e is PassportAlreadyRegisteredByOtherPK && !eDocument.dg15.isNullOrEmpty()) {
                    Log.d("Second without DG15", "without dg 15")
                    doc = eDocument.copy(dg15 = "")
                    isDocumentRegistered(doc, proof)
                }

                throw e
            }

            if (!isDocumentRegistered) {
                Log.d("Before registration", doc.dg15.toString())
                // Disable for now. Only generate and save proof to send it via web rtc
                /*registrationManager.register(
                    proof, doc, registrationManager.masterCertProof.value!!, false, circuitName
                )*/
            }

            _state.value = PassportProofState.FINALIZING
            return proof
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error in registerByDocument", e)
            throw e
        }
    }

    private suspend fun isDocumentRegistered(eDocument: EDocument, proof: UniversalProof): Boolean {
        Log.d("DG15", eDocument.dg15.toString())
        val passportInfo = registrationManager.getPassportInfo(eDocument, proof)
        if (passportInfo == null) {
            return false
        }

        // In new architecture, multiple sessions can exist simultaneously
        // Registration should always proceed to create a new session for this device
        // Check if passport has any active sessions
        val hasActiveSessions = passportInfo.activeSessionCount.toLong() > 0

        if (hasActiveSessions) {
            ErrorHandler.logDebug(TAG, "Passport has ${passportInfo.activeSessionCount} active sessions")
        } else {
            ErrorHandler.logDebug(TAG, "Passport has no active sessions yet")
        }

        // Always return false to allow registration/session creation to proceed
        return false
    }

    private suspend fun lightRegistration(eDocument: EDocument): UniversalProof.Light {
        try {
            // Get local copy for smart cast
            val pkBytes = privateKeyBytes ?: throw IllegalStateException("privateKeyBytes is null")
            _state.value = PassportProofState.READING_DATA

            val registerIdentityCircuitName = eDocument.getRegisterIdentityLightCircuitName()
            ErrorHandler.logDebug(TAG, "registerIdentityCircuitName: $registerIdentityCircuitName")
            val registeredCircuitData = RegisteredCircuitData.fromValue(registerIdentityCircuitName)
                ?: throw IllegalStateException("Circuit $registerIdentityCircuitName is not supported")


            _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

            // Download circuit files
            val filePaths = withContext(Dispatchers.Default) {
                CircuitDownloader(application).downloadGrothFiles(registeredCircuitData) { progress, visibility ->
                    _downloadProgress.value = progress
                }
            } ?: throw DownloadCircuitError()


            val lightProof = withContext(Dispatchers.Default) {
                generateLightRegistrationProof(
                    filePaths, eDocument, pkBytes, registeredCircuitData
                )
            }

            _state.value = PassportProofState.CREATING_CONFIDENTIAL_PROFILE

            val registerResponse = registrationManager.lightRegistration(eDocument, lightProof)
            val profile = identityManager.getProfiler()
            val currentIdentityKey = profile.publicKeyHash

            val universalProof =
                UniversalProofFactory.fromLight(registerResponse.data.attributes, lightProof)

            val passportInfo = withContext(Dispatchers.IO) {
                registrationManager.getPassportInfo(eDocument, universalProof)
            }

            // In new architecture, registration always proceeds to create a new session
            // Just log the session count for debugging
            if (passportInfo != null) {
                ErrorHandler.logDebug(TAG, "Passport has ${passportInfo.activeSessionCount} active sessions")
            }
            delay(second * 2)
            _state.value = PassportProofState.FINALIZING
            val res = withContext(Dispatchers.IO) {
                registrationManager.lightRegisterRelayer(lightProof, registerResponse)
            }
            res
            registrationManager.setRegistrationProof(universalProof)
            identityManager.setLightRegistrationData(registerResponse.data.attributes)
            delay(second)
            return UniversalProof.fromLight(registerResponse.data.attributes, lightProof)
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error in lightRegistration", e)
            throw e
        }
    }

    fun setAlreadyRegisteredByOtherPK() {
        _proofError.value = PassportAlreadyRegisteredByOtherPK()
    }

    suspend fun performRegistration(eDocument: EDocument): UniversalProof =
        withContext(managerScope.coroutineContext) {
            // If a registration is already in progress, return its result.
            currentRegistration?.let { ongoing ->
                if (ongoing.isActive) {
                    return@withContext ongoing.await()
                }
            }

            currentRegistration = managerScope.async {
                try {
                    resetState()
                    try {
                        registerCertificate(eDocument)
                    } catch (e: Exception) {
                        ErrorHandler.logError("certificate registration", "smth went wrong", e)
                    }

                    val proof = registerByDocument(eDocument)
                    identityManager.setRegistrationProof(proof)

                    if (!NOT_ALLOWED_COUNTRIES.contains(eDocument.personDetails?.nationality)) {
                        passportManager.updatePassportStatus(PassportStatus.ALLOWED)
                    } else {
                        passportManager.updatePassportStatus(PassportStatus.UNSUPPORTED_FOR_REWARDS)
                    }

                    proof
                } catch (e: Exception) {
                    when (e) {
                        is PassportAlreadyRegisteredByOtherPK -> {
                            ErrorHandler.logError(TAG, "Passport already registered", e)
                            _proofError.value = e
                            passportManager.updatePassportStatus(PassportStatus.ALREADY_REGISTERED_BY_OTHER_PK)
                            throw e
                        }

                        is DownloadCircuitError -> {
                            resetState()
                            ErrorHandler.logError(
                                TAG, "Error during default registration: ${e::class.simpleName}", e
                            )
                            _proofError.value = e
                            throw e
                        }

                        else -> {
                            ErrorHandler.logError(
                                TAG, "Default registration failed", e
                            )
                            resetState()
                            _proofError.value = e
                            throw e
                        }
                    }
                }
            }

            currentRegistration!!.await()
        }

    private suspend fun generateRegisterIdentityProofPlonk(
        eDocument: EDocument, registerIdentityCircuitType: RegisterIdentityCircuitType
    ): UniversalProof {
        val customDispatcher = Executors.newFixedThreadPool(1) { runnable ->
            Thread(null, runnable, "LargeStackThread", 100 * 1024 * 1024) // 100 MB stack size
        }.asCoroutineDispatcher()

        val circuitDownloader = CircuitNoirDownloader(application)

        ErrorHandler.logDebug("Plonk", "Plonk Start registration")

        val (trustedSetupPath, byteCodePath) = withContext(Dispatchers.IO) {
            val setupPath =
                circuitDownloader.downloadTrustedSetup(onProgressUpdate = { progress, isEnded ->
                    if (progress != _downloadProgress.value) {
                        _downloadProgress.value = progress
                    }
                })

            ErrorHandler.logDebug("Plonk", "Plonk Circuit downloaded")

            val circuitData = RegisterNoirCircuitData.fromValue(registerIdentityCircuitType.buildName())

            val codePath =
                circuitDownloader.downloadNoirByteCode(circuitData = circuitData!!) { progress, isEnded ->
                    if (_downloadProgress.value != progress) {
                        _downloadProgress.value = progress
                    }
                }

            Pair(setupPath, codePath)
        }

        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

        val inputs = buildPlonkRegistrationInputs(eDocument, registerIdentityCircuitType)

        return withContext(customDispatcher) {
            com.grnddsystems.celestials.util.NoirLock.mutex.lock()
            try {
                val circuitByteCode = File(byteCodePath).readText()

                val circuit = Circuit.fromJsonManifest(circuitByteCode)

                circuit.setupSrs(trustedSetupPath, false)

                ErrorHandler.logDebug("Plonk", "Start proving")

                val proof = circuit.prove(inputs, proofType = "plonk", recursive = false)

                val zk = UniversalProofFactory.fromPlonkBytes(Numeric.hexStringToByteArray(proof.proof))

                zk
            } finally {
                com.grnddsystems.celestials.util.NoirLock.mutex.unlock()
            }
        }
    }

    private suspend fun generateRegisterIdentityProofGroth(
        eDocument: EDocument, registerIdentityCircuitType: RegisterIdentityCircuitType
    ): UniversalProof {

        val circuitName = getCircuitName(registerIdentityCircuitType)
        val circuitData = getCircuitData(circuitName)

        val circuitDownloader = CircuitDownloader(application)

        val filePaths = withContext(Dispatchers.Default) {
            circuitDownloader.downloadGrothFiles(circuitData) { progress, visibility ->
                _downloadProgress.value = progress
            }
        } ?: throw DownloadCircuitError()

        ErrorHandler.logDebug(TAG, "Generating Groth registration proof")

        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

        val inputs = buildGrothRegistrationInputs(eDocument, registerIdentityCircuitType)
        val assetContext: Context = application.createPackageContext("com.grnddsystems.celestials", 0)
        val assetManager = assetContext.assets
        val zkp = ZKPUseCase(application, assetManager)

        val proof = UniversalProofFactory.fromGroth(
            generateRegistrationProofByCircuitType(
                circuitData, filePaths, zkp, inputs
            )
        )
        return proof
    }

    private fun getCircuitType(eDocument: EDocument): RegisterIdentityCircuitType {
        return try {
            eDocument.getRegisterIdentityCircuitType()
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Cannot get RegisterIdentityCircuitType", e)
            throw e
        }
    }

    private fun getCircuitData(registerIdentityCircuitName: String): RegisteredCircuitData {
        ErrorHandler.logDebug(TAG, "registerIdentityCircuitName: $registerIdentityCircuitName")
        return RegisteredCircuitData.fromValue(registerIdentityCircuitName)
            ?: throw IllegalStateException("Circuit $registerIdentityCircuitName is not supported")
    }

    private fun getCircuitName(registerIdentityCircuitType: RegisterIdentityCircuitType): String {
        return try {
            registerIdentityCircuitType.buildName()
        } catch (e: Exception) {
            ErrorHandler.logError(
                TAG, "Cannot get circuit name from registerIdentityCircuitType", e
            )
            ErrorHandler.logError(TAG, Gson().toJson(registerIdentityCircuitType))
            throw e
        }
    }

    private fun generateLightRegistrationProof(
        filePaths: DownloadRequest,
        eDocument: EDocument,
        privateKey: ByteArray,
        circuitData: RegisteredCircuitData
    ): GrothProof {
        val inputs = Gson().toJson(getLightRegistrationInputs(eDocument, privateKey)).toByteArray()
        val assetContext: Context = application.createPackageContext("com.grnddsystems.celestials", 0)
        val assetManager = assetContext.assets
        val zkp = ZKPUseCase(application, assetManager)
        return generateLightRegistrationProofByCircuitType(circuitData, filePaths, zkp, inputs)

    }

    private fun getLightRegistrationInputs(
        eDocument: EDocument, privateKey: ByteArray
    ): RegisterIdentityLightInputs {
        val digestAlgorithm = eDocument.getSodFile().digestAlgorithm
        val passportHashType = CircuitPassportHashType.fromValue(digestAlgorithm)
            ?: throw IllegalArgumentException("Invalid digest algorithm")
        val smartChunkingToBlockSize = passportHashType.getChunkSize()
        val dg1Chunks = CircuitUtil.smartChunking2(
            Numeric.hexStringToByteArray(eDocument.dg1), 1, smartChunkingToBlockSize.toLong()
        )
        return RegisterIdentityLightInputs(
            skIdentity = Numeric.toHexString(privateKey), dg1 = dg1Chunks
        )
    }

    private suspend fun buildGrothRegistrationInputs(
        eDocument: EDocument, circuitType: RegisterIdentityCircuitType
    ): ByteArray {
        // Get local copy for smart cast
        val pkBytes = privateKeyBytes ?: throw IllegalStateException("privateKeyBytes is null")

        val gson = GsonBuilder().setPrettyPrinting().create()
        val sodFile = eDocument.getSodFile()
        val cert = sodFile.docSigningCertificate
        val certPem = SecurityUtil.convertToPEM(cert)
        val certificatesSMTAddress = BaseConfig.CERTIFICATES_SMT_CONTRACT_ADDRESS
        val x509Utils = X509Util() // Adjust import if needed

        val proof = withContext(Dispatchers.IO) {
            val icao = readICAO(application.applicationContext)
            val slaveCertificateIndex =
                x509Utils.getSlaveCertificateIndex(certPem.toByteArray(), icao)
            val indexHex = slaveCertificateIndex.toHexString()
            val contract = rarimoContractManager.getPoseidonSMT(certificatesSMTAddress)
            contract.getProof(indexHex.hexToByteArray()).send()
        }

        val encapsulatedContent = Numeric.hexStringToByteArray(sodFile.readASN1Data())
        val signedAttributes = sodFile.eContent
        val publicKey = sodFile.getDocSigningCertificate().publicKey
        val signature = sodFile.encryptedDigest

        val pubKeyData = CryptoUtilsPassport.getDataFromPublicKey(publicKey)
            ?: throw IllegalArgumentException("Invalid public key data")
        val smartChunkingNumber = CircuitUtil.calculateSmartChunkingNumber(pubKeyData.size * 8)
        val smartChunkingToBlockSize = circuitType.passportHashType.getChunkSize()

        val dg15: List<Long> = if (eDocument.dg15.isNullOrEmpty()) {
            listOf()
        } else {
            CircuitUtil.smartChunking2(
                eDocument.dg15!!.decodeHexString(),
                circuitType.aaType!!.dg15ChunkNumber.toLong(),
                smartChunkingToBlockSize.toLong()
            )
        }

        val encapsulatedChunks = CircuitUtil.smartChunking2(
            encapsulatedContent,
            circuitType.ecChunkNumber.toLong(),
            smartChunkingToBlockSize.toLong()
        )

        val signedAttrChunks = CircuitUtil.smartChunking2(
            signedAttributes, 2, smartChunkingToBlockSize.toLong()
        )

        ErrorHandler.logDebug(TAG, "Signed attributes chunks: ${Gson().toJson(signedAttrChunks)}")

        val pubKeyChunks = when (publicKey) {
            is ECPublicKey -> pubKeyData.toBits().map { it.toString() }
            else -> CircuitUtil.smartChunking(BigInteger(1, pubKeyData), smartChunkingNumber)
                .map { it.toString() }
        }

        val signatureChunks = when (publicKey) {
            is ECPublicKey -> {
                CircuitUtil.parseECDSASignature(signature)?.toBits()?.map { it.toString() }
                    ?: throw Exception("Invalid ECDSA signature")
            }

            else -> CircuitUtil.smartChunking(BigInteger(1, signature), smartChunkingNumber)
                .map { it.toString() }
        }

        val dg1Chunks = CircuitUtil.smartChunking2(
            eDocument.dg1!!.decodeHexString(), 2, smartChunkingToBlockSize.toLong()
        )

        val inputs = RegisterIdentityInputs(
            skIdentity = Numeric.toHexStringWithPrefix(BigInteger(pkBytes)),
            encapsulatedContent = encapsulatedChunks,
            signedAttributes = signedAttrChunks,
            pubkey = pubKeyChunks,
            signature = signatureChunks,
            dg1 = dg1Chunks,
            dg15 = dg15,
            slaveMerkleRoot = (BigInteger(proof.root)).toString(),
            slaveMerkleInclusionBranches = proof.siblings.map { BigInteger(it).toString() })
        registrationManager.setMasterCertProof(proof)
        return gson.toJson(inputs).toByteArray()
    }


    private suspend fun buildPlonkRegistrationInputs(
        eDocument: EDocument, circuitType: RegisterIdentityCircuitType
    ): Map<String, Any> = withContext(Dispatchers.IO) {
        // Get local copy for smart cast
        val pkBytes = privateKeyBytes ?: throw IllegalStateException("privateKeyBytes is null")

        val sodFile = eDocument.getSodFile()
        val toHexList: (ByteArray) -> List<String> = { bytes ->
            bytes.map { byte -> Numeric.toHexString(byteArrayOf(byte)) }
        }

        coroutineScope {
            val proofDeferred = async {
                val cert = sodFile.docSigningCertificate
                val certPem = SecurityUtil.convertToPEM(cert)
                val icao = readICAO(application.applicationContext)
                val x509Utils = X509Util()
                val slaveCertificateIndex =
                    x509Utils.getSlaveCertificateIndex(certPem.toByteArray(), icao)
                val contract =
                    rarimoContractManager.getPoseidonSMT(BaseConfig.CERTIFICATES_SMT_CONTRACT_ADDRESS)
                val proof = contract.getProof(slaveCertificateIndex.toHexString().hexToByteArray()).send()
                proof
            }


            val publicKey = sodFile.docSigningCertificate.publicKey
            val sigBytes = sodFile.encryptedDigest
            val pubKeyData = CryptoUtilsPassport.getDataFromPublicKey(publicKey)!!
            val (pk, reductionPk, sig) = processSignatureData(circuitType, pubKeyData, sigBytes)


            // SHA-prepad inputs to match circuit's prepadded hash functions
            val dgHashType = circuitType.passportHashType
            val sigHashType = circuitType.signatureType.hashAlgorithm

            val dg1Padded = CircuitUtil.shaPrepad(eDocument.dg1!!.decodeHexString(), dgHashType)
            val dg15Padded = eDocument.dg15?.decodeHexString()?.let {
                CircuitUtil.shaPrepad(it, dgHashType)
            }
            val ecPadded = CircuitUtil.shaPrepad(
                Numeric.hexStringToByteArray(sodFile.readASN1Data()), sigHashType
            )
            val saPadded = CircuitUtil.shaPrepad(sodFile.eContent, sigHashType)

            val dg1Deferred = toHexList(dg1Padded)
            val dg15Deferred = dg15Padded?.let(toHexList) ?: listOf()
            val ecDeferred = toHexList(ecPadded)
            val saDeferred = toHexList(saPadded)
            val skIdentityDeferred = Numeric.toHexString(pkBytes)


            val proof = proofDeferred.await()

            registrationManager.setMasterCertProof(proof)

            mapOf(
                "dg15" to dg15Deferred,
                "sa" to saDeferred,
                "pk" to pk,
                "icao_root" to Numeric.toHexString(proof.root),
                "inclusion_branches" to proof.siblings.map { Numeric.toHexString(it) },
                "ec" to ecDeferred,
                "sk_identity" to skIdentityDeferred,
                "dg1" to dg1Deferred,
                "sig" to sig,
                "reduction_pk" to reductionPk
            )
        }
    }

    private fun readICAO(context: Context): ByteArray? {
        return try {
            val assetContext: Context = context.createPackageContext("com.grnddsystems.celestials", 0)
            assetContext.assets.open("masters_asset.pem").use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: IOException) {
            ErrorHandler.logError(TAG, "Error reading ICAO", e)
            null
        }
    }

    private fun processSignatureData(
        circuitType: RegisterIdentityCircuitType, pubKeyData: ByteArray, sigBytes: ByteArray
    ): Triple<List<String>, List<String>, List<String>> {
        val toHex: (BigInteger) -> String = { Numeric.toHexString(it.toByteArray()) }

        return when (circuitType.signatureType.algorithm) {
            CircuitAlgorithmType.RSA, CircuitAlgorithmType.RSAPSS -> {
                val pk = CircuitUtil.splitBy120Bits(pubKeyData).map(toHex)
                val reductionPk = CircuitUtil.rsaBarrettReductionParam(
                    BigInteger(1, pubKeyData), pubKeyData.size * 8
                ).map(toHex)
                val sig = CircuitUtil.splitBy120Bits(sigBytes).map(toHex)
                Triple(pk, reductionPk, sig)
            }

            CircuitAlgorithmType.ECDSA -> {
                val half = pubKeyData.size / 2
                val pubKeyX = pubKeyData.copyOfRange(0, half)
                val pubKeyY = pubKeyData.copyOfRange(half, pubKeyData.size)

                val pk =
                    (CircuitUtil.splitBy120Bits(pubKeyX) + CircuitUtil.splitBy120Bits(pubKeyY)).map(
                        toHex
                    )
                val reductionPk =
                    (CircuitUtil.splitEmptyData(pubKeyX) + CircuitUtil.splitEmptyData(pubKeyY)).map(
                        toHex
                    )

                val coordByteSize = pubKeyData.size / 2
                val sigBytes64 = CircuitUtil.parseECDSASignature(sigBytes, coordByteSize)!!
                val sigHalf = sigBytes64.size / 2
                val sigR = sigBytes64.copyOfRange(0, sigHalf)
                val sigS = sigBytes64.copyOfRange(sigHalf, sigBytes64.size)

                val sig =
                    (CircuitUtil.splitBy120Bits(sigR) + CircuitUtil.splitBy120Bits(sigS)).map(toHex)
                Triple(pk, reductionPk, sig)
            }
        }
    }

}