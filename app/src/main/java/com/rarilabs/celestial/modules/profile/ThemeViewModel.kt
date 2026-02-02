package com.rarilabs.celestial.modules.profile

import androidx.lifecycle.ViewModel
import com.rarilabs.celestial.data.enums.AppColorScheme
import com.rarilabs.celestial.manager.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {
    fun onColorSchemeChange(scheme: AppColorScheme) {
        settingsManager.updateColorScheme(scheme)


    }


    val colorScheme = settingsManager.colorScheme
}