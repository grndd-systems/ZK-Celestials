package com.rarilabs.celestial.modules.you

import androidx.lifecycle.ViewModel
import com.rarilabs.celestial.data.enums.PassportStatus
import com.rarilabs.celestial.manager.PassportManager
import com.rarilabs.celestial.modules.passportScan.models.EDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ZkIdentityNoPassportViewModel @Inject constructor(val passportManager: PassportManager) :
    ViewModel() {

    fun setJsonEDocument(eDocument: EDocument) {
        passportManager.setPassport(eDocument)
        passportManager.updatePassportStatus(status = PassportStatus.UNREGISTERED)
    }

}