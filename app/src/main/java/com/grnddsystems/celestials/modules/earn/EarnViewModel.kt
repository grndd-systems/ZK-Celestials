package com.grnddsystems.celestials.modules.earn

import androidx.lifecycle.ViewModel
import com.grnddsystems.celestials.manager.SettingsManager
import com.grnddsystems.celestials.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EarnViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val walletManager: WalletManager
) : ViewModel() {

    val colorScheme = settingsManager.colorScheme

    val pointsAsset = walletManager.pointsToken

}