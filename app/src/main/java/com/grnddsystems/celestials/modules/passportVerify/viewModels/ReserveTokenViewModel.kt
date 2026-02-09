package com.grnddsystems.celestials.modules.passportVerify.viewModels

import androidx.lifecycle.ViewModel
import com.grnddsystems.celestials.manager.AuthManager
import com.grnddsystems.celestials.manager.PassportManager
import com.grnddsystems.celestials.manager.PointsManager
import com.grnddsystems.celestials.manager.WalletManager
import com.grnddsystems.celestials.store.SecureSharedPrefsManager
import com.grnddsystems.celestials.util.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReserveTokenViewModel @Inject constructor(
    val walletManager: WalletManager,
    private val passportManager: PassportManager,
    val pointsManager: PointsManager,
    val authManager: AuthManager,
    private val sharedPrefsManager: SecureSharedPrefsManager
) : ViewModel() {

    suspend fun reserve() {
        pointsManager.verifyPassport()
        walletManager.loadBalances()
    }

    fun setAlreadyReserved() {
        sharedPrefsManager.saveIsAlreadyReserved(true)
    }

    fun getFlag(): String {
        return Country.fromISOCode(passportManager.getIsoCode()!!).flag
    }
}