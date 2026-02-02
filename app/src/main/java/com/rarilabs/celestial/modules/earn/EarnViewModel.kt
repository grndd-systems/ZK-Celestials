package com.rarilabs.celestial.modules.earn

import androidx.lifecycle.ViewModel
import com.rarilabs.celestial.manager.SettingsManager
import com.rarilabs.celestial.manager.WalletManager
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