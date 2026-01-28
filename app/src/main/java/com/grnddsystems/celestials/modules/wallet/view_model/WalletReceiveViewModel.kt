package com.grnddsystems.celestials.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.grnddsystems.celestials.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WalletReceiveViewModel @Inject constructor(
    private val walletManager: WalletManager
) : ViewModel() {
    val selectedWalletAsset = walletManager.selectedWalletAsset
}