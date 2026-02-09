package com.grnddsystems.celestials.ui.components.enter_program.components

import androidx.lifecycle.ViewModel
import com.grnddsystems.celestials.manager.PointsManager
import com.grnddsystems.celestials.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val pointsManager: PointsManager,
    private val walletManager: WalletManager
) : ViewModel() {
    suspend fun createNLoadBalance(referralCode: String) {
        pointsManager.createPointsBalance(referralCode)
        walletManager.loadBalances()
    }

    val getDeferredReferralCode = pointsManager::getDeferredReferralCode

    suspend fun loadBalance() {
        pointsManager.getPointsBalance()
    }
}