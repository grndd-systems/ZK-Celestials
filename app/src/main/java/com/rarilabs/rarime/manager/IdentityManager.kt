package com.rarilabs.rarime.manager

import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.registration.models.LightRegistrationData
import com.rarilabs.rarime.contracts.rarimo.FaceRegistry
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.data.UniversalProof
import com.rarilabs.rarime.util.decodeHexString
import identity.Identity
import identity.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.web3j.crypto.Credentials
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalStdlibApi::class)
class IdentityManager @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager,
    private val rarimoContractManager: RarimoContractManager,
) {
    private val _privateKey = MutableStateFlow(dataStoreManager.readPrivateKey())
    val privateKey: StateFlow<String?>
        get() = _privateKey.asStateFlow()

    var _registrationProof = MutableStateFlow(dataStoreManager.readUniversalProof())
        private set
    val registrationProof: StateFlow<UniversalProof?>
        get() = _registrationProof.asStateFlow()

    val privateKeyBytes: ByteArray?
        get() = _privateKey.value?.decodeHexString()

    private var _isLogsDeleted = MutableStateFlow(dataStoreManager.readIsLogsDeleted())
    val isLogsDeleted: StateFlow<Boolean>
        get() = _isLogsDeleted.asStateFlow()

    fun updateIsLogsDeleted(isLogsDeleted: Boolean) {
        _isLogsDeleted.value = isLogsDeleted
        dataStoreManager.saveIsLogsDeleted(isLogsDeleted)
    }

    fun setRegistrationProof(proof: UniversalProof?) {
        _registrationProof.value = proof

        proof?.let {
            dataStoreManager.saveUniversalProof(proof)
        }
    }

    fun setLightRegistrationData(data: LightRegistrationData?) {
        data?.let {
            dataStoreManager.saveLightRegistrationData(it)
        }
    }

    fun getProfiler(): Profile {
        return Profile().newProfile(privateKeyBytes)
    }

    fun rarimoAddress(): String {
        return getProfiler().rarimoAddress
    }

    fun evmAddress(): String {
        if (_privateKey.value != null)
            return Credentials.create(_privateKey.value).address
        return ""
    }

    fun getUserAirDropNullifier(): String {
        return getProfiler().calculateEventNullifierInt(BaseConfig.AIRDROP_SVC_ID)
    }

    fun getNullifierForFaceLikeness(): String {
        return getProfiler().calculateEventNullifierHex(FaceRegistry.EVENT_ID)
    }

    fun getUserAirDropNullifierHex(): String {
        return "0x" + BigInteger(this.getUserAirDropNullifier())
            .toByteArray()
            .toHexString()
    }

    fun getUserPointsNullifier(): String {
        return getProfiler().calculateEventNullifierInt(BaseConfig.POINTS_SVC_ID)
    }

    fun getUserPointsNullifierHex(): String {
        return "0x" + BigInteger(this.getUserPointsNullifier())
            .toByteArray()
            .toHexString()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun genPrivateKey(): String {
        return Identity.newBJJSecretKey().toHexString()
    }

    fun savePrivateKey(pk: String) {
        _privateKey.value = pk
        dataStoreManager.savePrivateKey(pk)
    }


    @OptIn(ExperimentalStdlibApi::class)
    suspend fun getPassportActiveIdentity(eDocument: EDocument): String? {
        try {
            val passportInfoKey: String = if (eDocument.dg15.isNullOrEmpty()) {
                registrationProof.value!!.getPassportHash()
            } else {
                registrationProof.value!!.getPublicKey()
            }

            var passportInfoKeyBytes = Identity.bigIntToBytes(passportInfoKey)

            if (passportInfoKeyBytes.size != 32) {
                passportInfoKeyBytes = passportInfoKeyBytes.copyOf(32)
            }

            val stateKeeperContract = rarimoContractManager.getStateKeeper()

            // Get all sessions for this passport
            val sessionsInfo = withContext(Dispatchers.IO) {
                stateKeeperContract.getPassportSessionsInfo(passportInfoKeyBytes).send()
            }

            val sessionKeys = sessionsInfo.component1() // bytes32[] sessionKeys_
            val sessionInfos = sessionsInfo.component2() // SessionInfo[] sessionInfos_

            ErrorHandler.logDebug("getPassportActiveIdentity", "Passport has ${sessionKeys.size} sessions")

            // Get our identity key from registration proof
            val ourIdentityKey = registrationProof.value!!.getIdentityKey()
            var ourIdentityKeyBytes = Identity.bigIntToBytes(ourIdentityKey)

            if (ourIdentityKeyBytes.size != 32) {
                ourIdentityKeyBytes = ourIdentityKeyBytes.copyOf(32)
            }

            // Check if our identity key is among the sessions
            for (i in sessionKeys.indices) {
                if (sessionKeys[i].contentEquals(ourIdentityKeyBytes)) {
                    ErrorHandler.logDebug("getPassportActiveIdentity", "Found our identity key at session index $i")
                    return org.web3j.utils.Numeric.toHexString(sessionKeys[i])
                }
            }

            ErrorHandler.logDebug("getPassportActiveIdentity", "Our identity key not found in sessions")
            return null
        } catch (e: Exception) {
            ErrorHandler.logError("getPassportActiveIdentity", e.message.toString(), e)
            return null
        }
    }

}