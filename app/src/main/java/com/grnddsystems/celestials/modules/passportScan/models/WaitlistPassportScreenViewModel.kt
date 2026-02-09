package com.grnddsystems.celestials.modules.passportScan.models

import androidx.lifecycle.ViewModel
import com.grnddsystems.celestials.manager.IdentityManager
import com.grnddsystems.celestials.store.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WaitlistPassportScreenViewModel @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager,
    private val identityManager: IdentityManager
) : ViewModel() {
    fun joinWaitlist() {
        dataStoreManager.saveIsInWaitlist(true)
    }

    fun getNullifierHex(): String {
        return identityManager.getUserPointsNullifierHex()
    }
}