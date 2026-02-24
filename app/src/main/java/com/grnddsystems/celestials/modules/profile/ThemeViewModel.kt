package com.grnddsystems.celestials.modules.profile

import androidx.lifecycle.ViewModel
import com.grnddsystems.celestials.data.enums.AppColorScheme
import com.grnddsystems.celestials.manager.SettingsManager
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