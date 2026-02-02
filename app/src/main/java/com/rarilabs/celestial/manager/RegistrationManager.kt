package com.rarilabs.celestial.manager

import RegisterIdentityCircuitType
import android.util.Log
import com.google.gson.Gson
import com.rarilabs.celestial.BaseConfig
import com.rarilabs.celestial.api.registration.RegistrationAPIManager
import com.rarilabs.celestial.api.registration.UserAlreadyRevoked
import com.rarilabs.celestial.api.registration.models.VerifySodResponse
import com.rarilabs.celestial.contracts.rarimo.PoseidonSMT.Proof
import com.rarilabs.celestial.contracts.rarimo.StateKeeper
import com.rarilabs.celestial.data.enums.PassportStatus
import com.rarilabs.celestial.modules.passportScan.models.EDocument
import com.rarilabs.celestial.util.Constants.NOT_ALLOWED_COUNTRIES
import com.rarilabs.celestial.util.Dg15FileOwn
import com.rarilabs.celestial.util.ErrorHandler
import com.rarilabs.celestial.util.data.GrothProof
import com.rarilabs.celestial.util.data.UniversalProof
import com.rarilabs.celestial.util.decodeHexString
import com.rarilabs.celestial.util.publicKeyToPem
import identity.CallDataBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.web3j.utils.Numeric
import javax.inject.Inject

class RegistrationManager @Inject constructor(
    private val registrationAPIManager: RegistrationAPIManager,
    private val rarimoContractManager: RarimoContractManager,
    private val passportManager: PassportManager,
    private val identityManager: IdentityManager
) {
    private var _masterCertProof = MutableStateFlow<Proof?>(null)
    val masterCertProof: StateFlow<Proof?>
        get() = _masterCertProof.asStateFlow()

    private var _activeIdentity = MutableStateFlow(ByteArray(0))
    val activeIdentity: StateFlow<ByteArray>
        get() = _activeIdentity.asStateFlow()

    private var _registrationProof = MutableStateFlow<UniversalProof?>(null)
    val registrationProof: StateFlow<UniversalProof?>
        get() = _registrationProof.asStateFlow()

    var _revocationChallenge = MutableStateFlow<ByteArray>(ByteArray(0))
        private set
    val revocationChallenge: StateFlow<ByteArray?>
        get() = _revocationChallenge.asStateFlow()

    var _revocationCallData = MutableStateFlow(null as ByteArray?)
    val revocationCallData: StateFlow<ByteArray?>
        get() = _revocationCallData.asStateFlow()

    private var _eDocument = MutableStateFlow(null as EDocument?)
    val eDocument: StateFlow<EDocument?>
        get() = _eDocument.asStateFlow()

    private var _revEDocument = MutableStateFlow(null as EDocument?)
    val revEDocument: StateFlow<EDocument?>
        get() = _revEDocument.asStateFlow()

    private var circuitData: RegisterIdentityCircuitType? = null

    fun getCircuitData(): RegisterIdentityCircuitType? {
        return circuitData
    }

    fun setCircuitData(circuitData: RegisterIdentityCircuitType) {
        this.circuitData = circuitData
    }

    fun setRevEDocument(eDocument: EDocument) {
        _revEDocument.value = eDocument
    }

    fun setRegistrationProof(proof: UniversalProof) {
        _registrationProof.value = proof
    }

    fun setEDocument(eDocument: EDocument?) {
        _eDocument.value = eDocument
    }

    fun setMasterCertProof(proof: Proof) {
        _masterCertProof.value = proof
    }

    suspend fun relayerRegister(callData: ByteArray, destination: String) =
        registrationAPIManager.register(callData, destination)

    /**
     * if isUserRevoking is true, then this method is re-issuance for revoked passport
     * else it is registration for new passport
     */
    suspend fun register(
        zkProof: UniversalProof,
        eDocument: EDocument,
        masterCertProof: Proof,
        isUserRevoking: Boolean,
        registerIdentityCircuitName: String
    ) {
        _eDocument.value = eDocument

        // Log AA signature details
        ErrorHandler.logDebug("RegistrationManager", "=== AA Signature in register() ===")
        ErrorHandler.logDebug("RegistrationManager", "aaSignature present: ${eDocument.aaSignature != null}")
        if (eDocument.aaSignature != null) {
            ErrorHandler.logDebug("RegistrationManager", "aaSignature: ${Numeric.toHexString(eDocument.aaSignature)}")
            ErrorHandler.logDebug("RegistrationManager", "aaSignature length: ${eDocument.aaSignature!!.size}")
        }
        ErrorHandler.logDebug("RegistrationManager", "dg15 present: ${!eDocument.dg15.isNullOrEmpty()}")

        val pubKeyPem = if (!eDocument.dg15.isNullOrEmpty()) {
            eDocument.getDg15File()!!.publicKey.publicKeyToPem()
                .toByteArray()
        } else {
            byteArrayOf()
        }

        val encapsulatedContent =
            Numeric.hexStringToByteArray(eDocument.getSodFile().readASN1Data())

        val callDataBuilder = CallDataBuilder()

        val callData = when (zkProof) {
            is UniversalProof.Groth -> {
                callDataBuilder.buildRegisterCalldata(
                    zkProof.getProofJson().toByteArray(),
                    eDocument.aaSignature,
                    pubKeyPem,
                    encapsulatedContent.size.toLong() * 8L,
                    masterCertProof.root,
                    isUserRevoking,
                    registerIdentityCircuitName
                )
            }

            is UniversalProof.Light -> {
                callDataBuilder.buildRegisterCalldata(
                    zkProof.getProofJson().toByteArray(),
                    eDocument.aaSignature,
                    pubKeyPem,
                    encapsulatedContent.size.toLong() * 8L,
                    masterCertProof.root,
                    isUserRevoking,
                    registerIdentityCircuitName
                )
            }

            is UniversalProof.Plonk -> {

                Log.i("UniversalProof.Plonk", zkProof.proof.proof)

                callDataBuilder.buildNoirRegisterCalldata(
                    zkProof.proof.rawProof,
                    eDocument.aaSignature,
                    pubKeyPem,
                    encapsulatedContent.size.toLong() * 8L,
                    masterCertProof.root,
                    isUserRevoking,
                    registerIdentityCircuitName
                )
            }
        }

        withContext(Dispatchers.IO) {
            val response = relayerRegister(callData, BaseConfig.REGISTER_CONTRACT_ADDRESS)

            response.data.attributes.tx_hash.let {
                rarimoContractManager.checkIsTransactionSuccessful(it)
            }
        }
    }

    suspend fun lightRegistration(eDocument: EDocument, zkProof: GrothProof): VerifySodResponse {
        return registrationAPIManager.lightRegistration(eDocument, zkProof)
    }

    suspend fun getPassportInfo(
        eDocument: EDocument,
        zkProof: UniversalProof,
    ): StateKeeper.PassportInfo? {
        try {
            val stateKeeperContract = rarimoContractManager.getStateKeeper()

            val passportInfoKeyBytes =
                passportManager.getPassportInfoKeyBytes(eDocument, zkProof)

            val passportInfo = withContext(Dispatchers.IO) {
                stateKeeperContract.getPassportInfo(passportInfoKeyBytes).send()
            }

            return passportInfo
        } catch (e: Exception) {
            ErrorHandler.logError("RegistrationManager", "Error getting passport info", e)
            throw e
        }
    }

    suspend fun lightRegisterRelayer(zkProof: GrothProof, verifySodResponse: VerifySodResponse) {
        val signature = verifySodResponse.data.attributes.signature.let {
            it.ifEmpty {
                throw IllegalStateException("verifySodResponse.data.attributes.signature is empty")
            }
        }

        val passportHash = verifySodResponse.data.attributes.passport_hash.let {
            it.ifEmpty {
                throw IllegalStateException("verifySodResponse.data.attributes.passport_hash is empty")
            }
        }

        val publicKey = verifySodResponse.data.attributes.public_key.let {
            it.ifEmpty {
                throw IllegalStateException("verifySodResponse.data.attributes.public_key is null")
            }
        }

        val callDataBuilder = CallDataBuilder()
        val callData = callDataBuilder.buildRegisterSimpleCalldata(
            Gson().toJson(
                zkProof
            ).toByteArray(),
            Numeric.hexStringToByteArray(signature),
            Numeric.hexStringToByteArray(passportHash),
            Numeric.hexStringToByteArray(publicKey),
            verifySodResponse.data.attributes.verifier
        )


        withContext(Dispatchers.IO) {
            val response =
                relayerRegister(callData, BaseConfig.REGISTRATION_SIMPLE_CONTRACT_ADRRESS)

            Log.i("response", response.data.attributes.tx_hash)
            val txData = response.data.attributes.tx_hash.let {
                rarimoContractManager.checkIsTransactionSuccessful(it)
            }
            Log.i("response", txData.toString())
        }

    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun getRevocationChallenge(): ByteArray? {
        return withContext(Dispatchers.IO) {
            val passportInfo = withContext(Dispatchers.IO) {
                getPassportInfo(eDocument.value!!, registrationProof.value!!)
            }

            // Check if passport has active sessions
            val hasActiveSessions = passportInfo?.activeSessionCount?.toLong() ?: 0L > 0

            if (hasActiveSessions) {
                ErrorHandler.logDebug("Revoke", "Passport has active sessions: ${passportInfo?.activeSessionCount}")
            } else {
                ErrorHandler.logDebug("Revoke", "Passport has no active sessions")
            }

            // Registration should continue regardless of existing sessions
            // Multiple devices can have active sessions simultaneously
            // Return null as challenge is no longer needed in new architecture
            return@withContext null
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun buildRevocationCallData() {
        val dG15File = Dg15FileOwn(revEDocument.value!!.dg15!!.decodeHexString().inputStream())

        val pubKeyPem = dG15File.publicKey.publicKeyToPem()

        val callDataBuilder = CallDataBuilder()

        ErrorHandler.logDebug("buildRevocationCallData", pubKeyPem)

        Log.i("activeIdentity.value", activeIdentity.value.toHexString())

        val encapsulatedContent =
            Numeric.hexStringToByteArray(revEDocument.value!!.getSodFile().readASN1Data())

        val callData = callDataBuilder.buildRevoceCalldata(
            activeIdentity.value,
            revEDocument.value!!.aaSignature,
            pubKeyPem.toByteArray(),
            encapsulatedContent.size.toLong() * 8
        )

        ErrorHandler.logDebug("callData", callData.toString())
        _revocationCallData.value = callData
    }

    suspend fun revoke() {
        try {
            try {
                val txResponse = relayerRegister(
                    revocationCallData.value!!,
                    BaseConfig.REGISTER_CONTRACT_ADDRESS
                )

                txResponse.data.attributes.tx_hash.let {
                    rarimoContractManager.checkIsTransactionSuccessful(it)
                }
            } catch (e: Exception) {
                ErrorHandler.logError("RegistrationManager:revoke:", "Error: $e", e)

                if (e !is UserAlreadyRevoked) {
                    throw e
                }
            }

            ErrorHandler.logDebug("registrationProof.value!!", registrationProof.value!!.toString())
            ErrorHandler.logDebug("masterCertProof.value!!", masterCertProof.value!!.toString())

            register(
                registrationProof.value!!,
                eDocument.value!!,
                masterCertProof.value!!,
                true,
                getCircuitData()!!.buildName()
            )

            identityManager.setRegistrationProof(registrationProof.value)

            if (!NOT_ALLOWED_COUNTRIES.contains(eDocument.value!!.personDetails?.nationality)) {
                passportManager.updatePassportStatus(PassportStatus.ALLOWED)
            } else {
                passportManager.updatePassportStatus(PassportStatus.UNSUPPORTED_FOR_REWARDS)
            }

        } catch (e: Exception) {
            ErrorHandler.logError("RevocationStepViewModel", "Error: $e", e)
            throw e
        }
    }
}