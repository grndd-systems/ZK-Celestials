package com.grnddsystems.celestials.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.grnddsystems.celestials.modules.main.LocalColors
import com.grnddsystems.celestials.modules.main.RarimeColors

object RarimeTheme {
    val colors: RarimeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val typography: RarimeTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}