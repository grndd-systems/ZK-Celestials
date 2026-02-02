@file:OptIn(ExperimentalStdlibApi::class)

package com.rarilabs.rarime.manager

import CircuitAlgorithmType
import CircuitPassportHashType
import RegisterIdentityCircuitType
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.noirandroid.lib.Circuit
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.BuildConfig
import com.rarilabs.rarime.api.registration.PassportAlreadyRegisteredByOtherPK
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.modules.passportScan.CircuitDownloader
import com.rarilabs.rarime.modules.passportScan.CircuitNoirDownloader
import com.rarilabs.rarime.modules.passportScan.DownloadCircuitError
import com.rarilabs.rarime.modules.passportScan.DownloadRequest
import com.rarilabs.rarime.modules.passportScan.models.CryptoUtilsPassport
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.RegisterIdentityInputs
import com.rarilabs.rarime.modules.passportScan.models.RegisterIdentityLightInputs
import com.rarilabs.rarime.modules.passportScan.nfc.SODFileOwn
import com.rarilabs.rarime.util.Constants.NOT_ALLOWED_COUNTRIES
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.SecurityUtil
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.circuits.CircuitUtil
import com.rarilabs.rarime.util.circuits.RegisterNoirCircuitData
import com.rarilabs.rarime.util.circuits.RegisteredCircuitData
import com.rarilabs.rarime.util.data.GrothProof
import com.rarilabs.rarime.util.data.UniversalProof
import com.rarilabs.rarime.util.data.UniversalProofFactory
import com.rarilabs.rarime.util.decodeHexString
import com.rarilabs.rarime.util.generateLightRegistrationProofByCircuitType
import com.rarilabs.rarime.util.generateRegistrationProofByCircuitType
import com.rarilabs.rarime.util.toBits
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
import java.io.FileNotFoundException
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
    private val privateKeyBytes = identityManager.privateKeyBytes

    private fun resetState() {
        _state.value = PassportProofState.READING_DATA
        _proofError.value = null
    }

    private suspend fun registerCertificate(eDocument: EDocument) {
        try {
            ErrorHandler.logDebug(TAG, "Starting certificate registration")
            val sodStream = eDocument.sod!!.decodeHexString().inputStream()
            val sodFile = SODFileOwn(sodStream)
            val x509Util = X509Util()

            val slaveCertificate = SecurityUtil.convertToPEM(sodFile.docSigningCertificate)
            val certificatesSMTAddress = BaseConfig.CERTIFICATES_SMT_CONTRACT_ADDRESS
            ErrorHandler.logDebug(TAG, "Using certificates SMT contract address: $certificatesSMTAddress")
            val certificatesSMTContract =
                rarimoContractManager.getPoseidonSMT(certificatesSMTAddress)
            val icao = readICAO(application.applicationContext)
            if (icao == null) {
                ErrorHandler.logError(TAG, "Failed to read ICAO master certificate for certificate registration")
                throw IllegalStateException("ICAO master certificate not available")
            }

            val slaveCertificateIndex = x509Util.getSlaveCertificateIndex(
                slaveCertificate.toByteArray(), icao
            )
            ErrorHandler.logDebug(TAG, "Certificate index: ${Numeric.toHexString(slaveCertificateIndex)}")

            val proof = withContext(Dispatchers.IO) {
                try {
                    certificatesSMTContract.getProof(slaveCertificateIndex).send()
                } catch (e: Exception) {
                    ErrorHandler.logError(TAG, "Error calling getProof for certificate registration", e)
                    throw e
                }
            }
            if (proof?.existence == true) {
                ErrorHandler.logDebug(TAG, "Passport certificate is already registered")
                return
            }
            ErrorHandler.logDebug(TAG, "Certificate not found in SMT, registering...")
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
                ErrorHandler.logError(TAG, "Certificate registration transaction failed: ${response.data.attributes.tx_hash}")
                throw IllegalStateException("Certificate registration transaction failed")
            } else {
                ErrorHandler.logDebug(TAG, "Certificate registration transaction successful")
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
                generateRegisterIdentityProofPlonk(
                    eDocument, registerIdentityCircuitType = circuitType
                )
            } else {

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
                    ErrorHandler.logDebug(TAG, "Retrying registration without DG15")
                    doc = eDocument.copy(dg15 = "")
                    try {
                        isDocumentRegistered(doc, proof)
                    } catch (e2: Exception) {
                        ErrorHandler.logError(TAG, "Document still registered after removing DG15", e2)
                        throw e2
                    }
                } else {
                    throw e
                }
            }

            if (!isDocumentRegistered) {
                ErrorHandler.logDebug(TAG, "Document not registered, proceeding with registration")
                val masterCertProof = registrationManager.masterCertProof.value
                if (masterCertProof == null) {
                    ErrorHandler.logError(TAG, "Master certificate proof is null - cannot register")
                    throw IllegalStateException("Master certificate proof is required for registration")
                }
                ErrorHandler.logDebug(TAG, "Calling registration manager with circuit: $circuitName")
                registrationManager.register(
                    proof, doc, masterCertProof, false, circuitName
                )
                ErrorHandler.logDebug(TAG, "Registration completed successfully")
            } else {
                ErrorHandler.logDebug(TAG, "Document already registered, skipping registration")
            }

            _state.value = PassportProofState.FINALIZING
            return proof
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error in registerByDocument", e)
            throw e
        }
    }

    private suspend fun isDocumentRegistered(eDocument: EDocument, proof: UniversalProof): Boolean {
        try {
            ErrorHandler.logDebug(TAG, "Checking if document is already registered")
            val passportInfo = registrationManager.getPassportInfo(eDocument, proof)
            if (passportInfo == null) {
                ErrorHandler.logDebug(TAG, "Passport info is null - document not registered")
                return false
            }
            val passportInfoIdentity = passportInfo.component1()?.activeIdentity

            if (passportInfoIdentity == null) {
                ErrorHandler.logDebug(TAG, "Active identity is null - document not registered")
                return false
            }

            val ZERO_BYTES32 = ByteArray(32) { 0 }

            if (passportInfoIdentity.contentEquals(ZERO_BYTES32)) {
                ErrorHandler.logDebug(TAG, "Active identity is zero - document not registered")
                return false
            }

            val currentIdentityKey = identityManager.getProfiler().publicKeyHash
            val passportIdentityHex = passportInfoIdentity.toHexString()
            val currentIdentityHex = currentIdentityKey.toHexString()

            ErrorHandler.logDebug(TAG, "Passport identity: $passportIdentityHex")
            ErrorHandler.logDebug(TAG, "Current identity: $currentIdentityHex")

            if (passportIdentityHex == currentIdentityHex) {
                ErrorHandler.logDebug(TAG, "Passport is already registered with this PK")
                return true
            }

            ErrorHandler.logError(TAG, "Passport is already registered with a different PK. Passport: $passportIdentityHex, Current: $currentIdentityHex")
            throw PassportAlreadyRegisteredByOtherPK()
        } catch (e: PassportAlreadyRegisteredByOtherPK) {
            throw e
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error checking document registration status", e)
            // If we can't check, assume not registered and let registration proceed
            // The contract will reject if it's already registered
            return false
        }
    }

    private suspend fun lightRegistration(eDocument: EDocument): UniversalProof.Light {
        try {
            if (privateKeyBytes == null) {
                ErrorHandler.logError(TAG, "privateKeyBytes is null in lightRegistration")
                throw IllegalStateException("privateKeyBytes is null")
            }
            _state.value = PassportProofState.READING_DATA

            val registerIdentityCircuitName = eDocument.getRegisterIdentityLightCircuitName()
            ErrorHandler.logDebug(TAG, "Light registration circuit name: $registerIdentityCircuitName")
            val registeredCircuitData = RegisteredCircuitData.fromValue(registerIdentityCircuitName)
                ?: run {
                    ErrorHandler.logError(TAG, "Circuit $registerIdentityCircuitName is not supported for light registration")
                    throw IllegalStateException("Circuit $registerIdentityCircuitName is not supported")
                }

            _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

            // Download circuit files
            val filePaths = withContext(Dispatchers.Default) {
                try {
                    CircuitDownloader(application).downloadGrothFiles(registeredCircuitData) { progress, visibility ->
                        _downloadProgress.value = progress
                    }
                } catch (e: Exception) {
                    ErrorHandler.logError(TAG, "Failed to download Groth files for light registration", e)
                    throw DownloadCircuitError()
                }
            } ?: run {
                ErrorHandler.logError(TAG, "Circuit file paths are null for light registration")
                throw DownloadCircuitError()
            }
            ErrorHandler.logDebug(TAG, "Light registration circuit files downloaded")

            val lightProof = withContext(Dispatchers.Default) {
                try {
                    generateLightRegistrationProof(
                        filePaths, eDocument, privateKeyBytes, registeredCircuitData
                    )
                } catch (e: Exception) {
                    ErrorHandler.logError(TAG, "Failed to generate light registration proof", e)
                    throw e
                }
            }
            ErrorHandler.logDebug(TAG, "Light registration proof generated successfully")

            _state.value = PassportProofState.CREATING_CONFIDENTIAL_PROFILE

            val registerResponse = try {
                registrationManager.lightRegistration(eDocument, lightProof)
            } catch (e: Exception) {
                ErrorHandler.logError(TAG, "Failed to call lightRegistration API", e)
                throw e
            }
            val profile = identityManager.getProfiler()
            val currentIdentityKey = profile.publicKeyHash

            val universalProof =
                UniversalProofFactory.fromLight(registerResponse.data.attributes, lightProof)

            val passportInfo = withContext(Dispatchers.IO) {
                try {
                    registrationManager.getPassportInfo(eDocument, universalProof)
                } catch (e: Exception) {
                    ErrorHandler.logError(TAG, "Failed to get passport info for light registration", e)
                    null
                }
            }
            
            if (passportInfo != null) {
                val passportInfoKey = passportInfo.component1()
                if (passportInfoKey?.activeIdentity?.contentEquals(currentIdentityKey) == true) {
                    ErrorHandler.logDebug(TAG, "Passport is already registered with this PK (light registration)")
                    registrationManager.setRegistrationProof(universalProof)
                    identityManager.setLightRegistrationData(registerResponse.data.attributes)
                    return UniversalProof.fromLight(registerResponse.data.attributes, lightProof)
                }
            }
            
            delay(second * 2)
            _state.value = PassportProofState.FINALIZING
            val res = withContext(Dispatchers.IO) {
                try {
                    registrationManager.lightRegisterRelayer(lightProof, registerResponse)
                } catch (e: Exception) {
                    ErrorHandler.logError(TAG, "Failed to submit light registration to relayer", e)
                    throw e
                }
            }
            registrationManager.setRegistrationProof(universalProof)
            identityManager.setLightRegistrationData(registerResponse.data.attributes)
            delay(second)
            ErrorHandler.logDebug(TAG, "Light registration completed successfully")
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
                                TAG, "Default registration failed, trying light registration", e
                            )
                            try {
                                val lightProof = lightRegistration(eDocument)
                                identityManager.setRegistrationProof(lightProof)

                                if (!NOT_ALLOWED_COUNTRIES.contains(eDocument.personDetails?.nationality)) {
                                    passportManager.updatePassportStatus(PassportStatus.ALLOWED)
                                } else {
                                    passportManager.updatePassportStatus(PassportStatus.UNSUPPORTED_FOR_REWARDS)
                                }

                                lightProof
                            } catch (e2: Exception) {
                                when (e2) {
                                    is PassportAlreadyRegisteredByOtherPK -> {
                                        ErrorHandler.logError(
                                            TAG,
                                            "Passport already registered during light registration",
                                            e2
                                        )
                                        passportManager.updatePassportStatus(PassportStatus.ALREADY_REGISTERED_BY_OTHER_PK)
                                        _proofError.value = e2
                                        throw e2
                                    }

                                    is DownloadCircuitError -> {
                                        resetState()
                                        ErrorHandler.logError(
                                            TAG,
                                            "Connection/Unpacking error during light registration",
                                            e2
                                        )
                                        _proofError.value = e2
                                        throw e2
                                    }

                                    else -> {
                                        if (!NOT_ALLOWED_COUNTRIES.contains(eDocument.personDetails?.nationality)) {
                                            passportManager.updatePassportStatus(PassportStatus.WAITLIST)
                                        } else {
                                            passportManager.updatePassportStatus(PassportStatus.WAITLIST_UNSUPPORTED_FOR_REWARDS)
                                        }
                                        ErrorHandler.logError(TAG, "Light registration failed", e2)
                                        _proofError.value = e2
                                        throw e2
                                    }
                                }
                            }
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

        val circuitData = RegisterNoirCircuitData.fromValue(registerIdentityCircuitType.buildName())
        if (circuitData == null) {
            ErrorHandler.logError(TAG, "Circuit data is null for: ${registerIdentityCircuitType.buildName()}")
            throw IllegalStateException("Circuit data not found for ${registerIdentityCircuitType.buildName()}")
        }

        val trustedSetupPath = try {
            circuitDownloader.downloadTrustedSetup(onProgressUpdate = { progress, isEnded ->
                if (progress != _downloadProgress.value) {
                    _downloadProgress.value = progress
                }
            })
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Failed to download trusted setup for Plonk", e)
            throw e
        }
        ErrorHandler.logDebug("Plonk", "Trusted setup downloaded to: $trustedSetupPath")

        val byteCodePath = try {
            circuitDownloader.downloadNoirByteCode(circuitData = circuitData) { progress, isEnded ->
                if (_downloadProgress.value != progress) {
                    _downloadProgress.value = progress
                }
            }
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Failed to download Noir bytecode for circuit: ${circuitData.value}", e)
            throw e
        }
        ErrorHandler.logDebug("Plonk", "Noir bytecode downloaded to: $byteCodePath")

        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

        val inputs = try {
            buildPlonkRegistrationInputs(eDocument, registerIdentityCircuitType)
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Failed to build Plonk registration inputs", e)
            throw e
        }
        ErrorHandler.logDebug("Plonk", "Registration inputs built successfully")

        return withContext(customDispatcher) {
            try {
                val circuitByteCode = File(byteCodePath).readText()
                if (circuitByteCode.isEmpty()) {
                    ErrorHandler.logError(TAG, "Circuit bytecode is empty")
                    throw IllegalStateException("Circuit bytecode is empty")
                }

                val circuit = Circuit.fromJsonManifest(circuitByteCode)
                ErrorHandler.logDebug("Plonk", "Circuit loaded from manifest")

                val setupResult = try {
                    circuit.setupSrs(trustedSetupPath, false)
                } catch (e: Exception) {
                    ErrorHandler.logError(TAG, "Failed to setup SRS with trusted setup", e)
                    throw e
                }
                ErrorHandler.logDebug("Plonk", "SRS setup completed")

                ErrorHandler.logDebug("Plonk", "Start proving")
                val proof = try {
                    circuit.prove(inputs, proofType = "plonk", recursive = false)
                } catch (e: Exception) {
                    ErrorHandler.logError(TAG, "Proof generation failed", e)
                    throw e
                }
                if (proof.proof.isNullOrEmpty()) {
                    ErrorHandler.logError(TAG, "Generated proof is empty")
                    throw IllegalStateException("Generated proof is empty")
                }
                ErrorHandler.logDebug("Plonk", "Proof generated successfully, length: ${proof.proof.length}")

                val zk = UniversalProofFactory.fromPlonkBytes(Numeric.hexStringToByteArray(proof.proof))
                ErrorHandler.logDebug("Plonk", "Universal proof created from Plonk bytes")
                return@withContext zk
            } catch (e: Exception) {
                ErrorHandler.logError(TAG, "Error in Plonk proof generation", e)
                throw e
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
            try {
                circuitDownloader.downloadGrothFiles(circuitData) { progress, visibility ->
                    _downloadProgress.value = progress
                }
            } catch (e: Exception) {
                ErrorHandler.logError(TAG, "Failed to download Groth circuit files for: ${circuitData.value}", e)
                throw DownloadCircuitError()
            }
        } ?: run {
            ErrorHandler.logError(TAG, "Circuit file paths are null for: ${circuitData.value}")
            throw DownloadCircuitError()
        }
        ErrorHandler.logDebug(TAG, "Groth circuit files downloaded. Zkey: ${filePaths.zkey}, Dat: ${filePaths.dat}")

        ErrorHandler.logDebug(TAG, "Generating Groth registration proof")

        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

        val inputs = try {
            buildGrothRegistrationInputs(eDocument, registerIdentityCircuitType)
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Failed to build Groth registration inputs", e)
            throw e
        }
        ErrorHandler.logDebug(TAG, "Groth registration inputs built successfully, size: ${inputs.size} bytes")
        
        val assetContext: Context = application.createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets
        val zkp = ZKPUseCase(application, assetManager)

        val proof = try {
            val grothProof = generateRegistrationProofByCircuitType(
                circuitData, filePaths, zkp, inputs
            )
            ErrorHandler.logDebug(TAG, "Groth proof generated successfully")
            UniversalProofFactory.fromGroth(grothProof)
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Failed to generate Groth proof", e)
            throw e
        }
        ErrorHandler.logDebug(TAG, "Universal proof created from Groth proof")
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
        val assetContext: Context = application.createPackageContext("com.rarilabs.rarime", 0)
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
        val gson = GsonBuilder().setPrettyPrinting().create()
        val sodFile = eDocument.getSodFile()
        val cert = sodFile.docSigningCertificate
        val certPem = SecurityUtil.convertToPEM(cert)
        val certificatesSMTAddress = BaseConfig.CERTIFICATES_SMT_CONTRACT_ADDRESS
        val x509Utils = X509Util() // Adjust import if needed

        val proof = withContext(Dispatchers.IO) {
            try {
                val icao = readICAO(application.applicationContext)
                if (icao == null) {
                    ErrorHandler.logError(TAG, "Failed to read ICAO master certificate")
                    throw IllegalStateException("ICAO master certificate not available")
                }
                val slaveCertificateIndex =
                    x509Utils.getSlaveCertificateIndex(certPem.toByteArray(), icao)
                ErrorHandler.logDebug(TAG, "Fetching SMT proof for certificate index: ${Numeric.toHexString(slaveCertificateIndex)}")
                val contract = rarimoContractManager.getPoseidonSMT(certificatesSMTAddress)
                val proofResult = contract.getProof(slaveCertificateIndex).send()
                if (proofResult == null) {
                    ErrorHandler.logError(TAG, "SMT proof is null for certificate index")
                    throw IllegalStateException("SMT proof is null")
                }
                ErrorHandler.logDebug(TAG, "SMT proof fetched successfully. Root: ${Numeric.toHexString(proofResult.root)}, Siblings count: ${proofResult.siblings.size}")
                proofResult
            } catch (e: Exception) {
                ErrorHandler.logError(TAG, "Error fetching SMT proof for Groth registration", e)
                throw e
            }
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
            skIdentity = Numeric.toHexStringWithPrefix(BigInteger(privateKeyBytes)),
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

        val sodFile = eDocument.getSodFile()
        val toHexList: (ByteArray) -> List<String> = { bytes ->
            bytes.map { byte -> Numeric.toHexString(byteArrayOf(byte)) }
        }

        coroutineScope {
            val proofDeferred = async {
                try {
                    val cert = sodFile.docSigningCertificate
                    val certPem = SecurityUtil.convertToPEM(cert)
                    val icao = readICAO(application.applicationContext)
                    if (icao == null) {
                        ErrorHandler.logError(TAG, "Failed to read ICAO master certificate for Plonk")
                        throw IllegalStateException("ICAO master certificate not available")
                    }
                    val x509Utils = X509Util()
                    val slaveCertificateIndex =
                        x509Utils.getSlaveCertificateIndex(certPem.toByteArray(), icao)
                    ErrorHandler.logDebug(TAG, "Fetching SMT proof for Plonk certificate index: ${Numeric.toHexString(slaveCertificateIndex)}")
                    val contract =
                        rarimoContractManager.getPoseidonSMT(BaseConfig.CERTIFICATES_SMT_CONTRACT_ADDRESS)
                    val proofResult = contract.getProof(slaveCertificateIndex).send()
                    if (proofResult == null) {
                        ErrorHandler.logError(TAG, "SMT proof is null for Plonk certificate index")
                        throw IllegalStateException("SMT proof is null")
                    }
                    ErrorHandler.logDebug(TAG, "SMT proof fetched successfully for Plonk. Root: ${Numeric.toHexString(proofResult.root)}, Siblings count: ${proofResult.siblings.size}")
                    proofResult
                } catch (e: Exception) {
                    ErrorHandler.logError(TAG, "Error fetching SMT proof for Plonk registration", e)
                    throw e
                }
            }


            val publicKey = sodFile.docSigningCertificate.publicKey
            val sigBytes = sodFile.encryptedDigest
            val pubKeyData = CryptoUtilsPassport.getDataFromPublicKey(publicKey)!!
            val (pk, reductionPk, sig) = processSignatureData(circuitType, pubKeyData, sigBytes)


            val dg1Deferred = toHexList(eDocument.dg1!!.decodeHexString())
            val dg15Deferred = eDocument.dg15?.decodeHexString()?.let(toHexList) ?: listOf()
            val ecDeferred = toHexList(Numeric.hexStringToByteArray(sodFile.readASN1Data()))
            val saDeferred = toHexList(sodFile.eContent)
            val skIdentityDeferred = Numeric.toHexString(privateKeyBytes)

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
            // Use application context to ensure we get the correct assets
            val appContext = context.applicationContext ?: context
            appContext.assets.open("masters_asset.pem").use { inputStream ->
                val bytes = inputStream.readBytes()
                if (bytes.isEmpty()) {
                    ErrorHandler.logError(TAG, "ICAO master certificate file is empty")
                    return null
                }
                ErrorHandler.logDebug(TAG, "Successfully loaded ICAO master certificate (${bytes.size} bytes)")
                bytes
            }
        } catch (e: FileNotFoundException) {
            ErrorHandler.logError(TAG, "ICAO master certificate file not found: masters_asset.pem. Please ensure the file exists in app/src/main/assets/", e)
            null
        } catch (e: IOException) {
            ErrorHandler.logError(TAG, "Error reading ICAO master certificate", e)
            null
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Unexpected error reading ICAO master certificate", e)
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

                val sigBytes64 = CircuitUtil.parseECDSASignature(sigBytes)!!
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