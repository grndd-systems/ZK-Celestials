package com.rarilabs.celestial.modules.you

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rarilabs.celestial.data.enums.PassportCardLook
import com.rarilabs.celestial.data.enums.PassportIdentifier
import com.rarilabs.celestial.manager.PassportManager
import com.rarilabs.celestial.manager.ProofGenerationManager
import com.rarilabs.celestial.store.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ZkIdentityScreenViewModel @Inject constructor(
    private val app: Application,
    private val passportManager: PassportManager,
    private val sharedPrefsManager: SecureSharedPrefsManager,
    private val proofGenerationManager: ProofGenerationManager,
) : AndroidViewModel(app) {

    var passport = passportManager.passport
    var passportCardLook = passportManager.passportCardLook
    var passportIdentifiers = passportManager.passportIdentifiers
    var isIncognito = passportManager.isIncognitoMode


    val passportStatus = passportManager.passportStatus

    val performRegistration = proofGenerationManager::performRegistration
    val setAlreadyRegisteredByOtherPK = proofGenerationManager::setAlreadyRegisteredByOtherPK

    private val _uiState = MutableStateFlow(IdentityCardBottomBarUiState())
    val uiState: StateFlow<IdentityCardBottomBarUiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            launch {
                proofGenerationManager.state.collect { newState ->
                    _uiState.value = _uiState.value.copy(loadingState = newState)
                }
            }

            launch {
                proofGenerationManager.proofError.collect { newError ->
                    _uiState.value = _uiState.value.copy(proofError = newError)
                }
            }

            launch {
                passportManager.passportStatus.collect { newStatus ->
                    _uiState.value = _uiState.value.copy(passportStatus = newStatus)
                }
            }
        }
    }

    fun retryRegistration() {
        viewModelScope.launch {
            passportManager.passport.let {
                proofGenerationManager.performRegistration(passportManager.passport.value!!)
            }
        }
    }

    fun onPassportCardLookChange(passportCardLook: PassportCardLook) {
        passportManager.updatePassportCardLook(passportCardLook)
    }

    fun onIncognitoChange(isIncognito: Boolean) {
        passportManager.updateIsIncognitoMode(isIncognito)
    }

    fun onPassportIdentifiersChange(passportIdentifier: PassportIdentifier) {
        val passportIdentifiers = listOf(passportIdentifier)
        passportManager.updatePassportIdentifiers(passportIdentifiers)
    }

    fun getIsAlreadyReserved(): Boolean {
        return sharedPrefsManager.getIsAlreadyReserved()
    }

    fun getIdentityLegalScreensShown(): Boolean {
        return sharedPrefsManager.getIdentityLegalScreensShown()
    }

    fun saveIdentityLegalScreensShown(isShown: Boolean) {
        sharedPrefsManager.saveIdentityLegalScreensShown(isShown)
    }
}