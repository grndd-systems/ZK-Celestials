package com.rarilabs.celestial.modules.passportScan.models

import androidx.lifecycle.ViewModel
import com.rarilabs.celestial.api.registration.models.LightRegistrationData
import com.rarilabs.celestial.data.enums.PassportStatus
import com.rarilabs.celestial.manager.IdentityManager
import com.rarilabs.celestial.manager.PassportManager
import com.rarilabs.celestial.manager.PointsManager
import com.rarilabs.celestial.manager.RegistrationManager
import com.rarilabs.celestial.manager.WalletManager
import com.rarilabs.celestial.util.ErrorHandler
import com.rarilabs.celestial.util.data.UniversalProof
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