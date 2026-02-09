package com.grnddsystems.celestials.modules.main

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.grnddsystems.celestials.data.enums.AppColorScheme
import com.grnddsystems.celestials.ui.theme.RarimeTheme
import com.grnddsystems.celestials.util.Screen

private val navPureBgRoutes = listOf(
    Screen.Register.NewIdentity.route,
    Screen.Main.Wallet.Send.route,
    Screen.Main.Wallet.route,
    Screen.Lock.route,
    Screen.Main.Rewards.RewardsClaim.route,
    Screen.Main.Rewards.RewardsEventsItem.route,
)

// TODO: rollback once there will be any screen with pure bg
// private val statusPureBgRoutes = listOf()

@Composable
fun ScreenBarsColor(colorScheme: AppColorScheme, route: String) {
    val view = LocalView.current
    val isSystemDark = isSystemInDarkTheme()

    val navColor =
        if (route in navPureBgRoutes) RarimeTheme.colors.backgroundPure
        else RarimeTheme.colors.backgroundPrimary
//    TODO: rollback once there will be any screen with pure bg
//    val statusColor =
//        if (route in statusPureBgRoutes) RarimeTheme.colors.backgroundPure
//        else RarimeTheme.colors.backgroundPrimary
    val statusColor = RarimeTheme.colors.backgroundPrimary

    val isLightStyle = when (colorScheme) {
        AppColorScheme.LIGHT -> true
        AppColorScheme.DARK -> false
        AppColorScheme.SYSTEM -> !isSystemDark
    }

    fun changeBarsStyle() {
        val insetsController =
            WindowCompat.getInsetsController((view.context as Activity).window, view)
        insetsController.isAppearanceLightStatusBars = isLightStyle
        insetsController.isAppearanceLightNavigationBars = isLightStyle
    }

    if (!view.isInEditMode) {
        SideEffect {
            changeBarsStyle()
        }
    }
}