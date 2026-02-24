package com.grnddsystems.celestials.modules.security

import androidx.lifecycle.ViewModel
import com.grnddsystems.celestials.data.enums.SecurityCheckState
import com.grnddsystems.celestials.manager.SecurityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LockViewModule @Inject constructor(
    private val securityManager: SecurityManager
) : ViewModel() {
    val lockTimestamp = securityManager.lockTimestamp

    var passcode = securityManager.passcode

    var isPasscodeEnabled = securityManager.passcodeState.value == SecurityCheckState.ENABLED
    var isBiometricEnabled = securityManager.biometricsState.value == SecurityCheckState.ENABLED

    fun lockPasscode() {
        securityManager.lockPasscode()
    }

    fun unlockScreen() {
        securityManager.unlockScreen()
    }

}