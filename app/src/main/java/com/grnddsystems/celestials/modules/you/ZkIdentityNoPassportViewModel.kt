package com.grnddsystems.celestials.modules.you

import androidx.lifecycle.ViewModel
import com.grnddsystems.celestials.data.enums.PassportStatus
import com.grnddsystems.celestials.manager.PassportManager
import com.grnddsystems.celestials.modules.passportScan.models.EDocument
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