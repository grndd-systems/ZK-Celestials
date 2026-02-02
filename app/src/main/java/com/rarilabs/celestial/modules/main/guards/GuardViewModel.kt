package com.rarilabs.celestial.modules.main.guards

import androidx.lifecycle.ViewModel
import com.rarilabs.celestial.manager.AuthManager
import com.rarilabs.celestial.manager.IdentityManager
import com.rarilabs.celestial.manager.SecurityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GuardViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val securityManager: SecurityManager,
    private val identityManager: IdentityManager,
) : ViewModel() {
    val privateKey = identityManager.privateKey

    val isScreenLocked = securityManager.isScreenLocked
    val biometricsState = securityManager.biometricsState
    var passcodeState = securityManager.passcodeState
}