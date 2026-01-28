package com.grnddsystems.celestials.modules.passportScan.models

import androidx.lifecycle.ViewModel
import com.grnddsystems.celestials.api.registration.models.LightRegistrationData
import com.grnddsystems.celestials.data.enums.PassportStatus
import com.grnddsystems.celestials.manager.IdentityManager
import com.grnddsystems.celestials.manager.PassportManager
import com.grnddsystems.celestials.manager.PointsManager
import com.grnddsystems.celestials.manager.RegistrationManager
import com.grnddsystems.celestials.manager.WalletManager
import com.grnddsystems.celestials.util.ErrorHandler
import com.grnddsystems.celestials.util.data.UniversalProof
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanPassportScreenViewModel @Inject constructor(
    private val passportManager: PassportManager,
    private val identityManager: IdentityManager,
    private val registrationManager: RegistrationManager,
    private val walletManager: WalletManager,
    private val pointsManager: PointsManager
) : ViewModel() {
    val eDocument = registrationManager.eDocument
    val pointsToken = walletManager.pointsToken

    fun rejectRevocation() {
        ErrorHandler.logDebug("ScanPassportScreenViewModel", "rejectRevocation")
        resetPassportState()
    }

    suspend fun isVerified(): Boolean {
        return pointsManager.getPointsBalance()?.data?.attributes?.is_verified ?: false
    }

    fun resetPassportState() {
        ErrorHandler.logDebug("ScanPassportScreenViewModel", "resetPassportState")
        passportManager.deletePassport()
    }

    fun finishRevocation() {
        ErrorHandler.logDebug("ScanPassportScreenViewModel", "finishRevocation")
        savePassport()
        saveRegistrationProof(registrationManager.registrationProof.value!!)
    }

    fun setPassportTEMP(eDocument: EDocument?) {
        registrationManager.setEDocument(eDocument)
    }

    fun savePassport() {
        registrationManager.eDocument.value?.let {
            passportManager.setPassport(eDocument.value)
            passportManager.updatePassportStatus(status = PassportStatus.UNREGISTERED)
        }
    }

    fun saveRegistrationProof(registrationProof: UniversalProof) {
        ErrorHandler.logDebug("ScanPassportScreenViewModel", "saveRegistrationProof")
        identityManager.setRegistrationProof(registrationProof)
    }

    fun saveLightRegistrationData(lightRegistrationData: LightRegistrationData?) {
        identityManager.setLightRegistrationData(lightRegistrationData)
    }
}