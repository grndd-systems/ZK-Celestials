package com.grnddsystems.celestials.ui.theme

import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.grnddsystems.celestials.data.enums.AppColorScheme
import com.grnddsystems.celestials.modules.main.LocalColors
import com.grnddsystems.celestials.modules.main.darkColors

@Composable
fun AppTheme(
    colorScheme: AppColorScheme = AppColorScheme.SYSTEM,
    content: @Composable () -> Unit,
) {
    val currentColors = darkColors()
    val rememberedColors =
        remember { currentColors.copy() }.apply { updateColorsFrom(currentColors) }
    CompositionLocalProvider(
        LocalColors provides rememberedColors,
        LocalTypography provides RarimeTypography(),
    ) {
        ProvideTextStyle(
            value = RarimeTheme.typography.body3.copy(color = RarimeTheme.colors.backgroundPrimary),
            content = content
        )
    }
}