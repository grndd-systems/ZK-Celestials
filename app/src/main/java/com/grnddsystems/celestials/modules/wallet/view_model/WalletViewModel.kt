package com.grnddsystems.celestials.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.grnddsystems.celestials.manager.WalletAsset
import com.grnddsystems.celestials.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletManager: WalletManager,
) : ViewModel() {

    val walletAssets = walletManager.walletAssets

    val selectedWalletAsset = walletManager.selectedWalletAsset

    fun updateSelectedWalletAsset(walletAsset: WalletAsset) {
        walletManager.setSelectedWalletAsset(walletAsset)
    }

    suspend fun updateBalances() {
        walletManager.loadBalances()
    }
}