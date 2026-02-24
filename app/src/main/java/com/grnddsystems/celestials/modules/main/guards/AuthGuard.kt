package com.grnddsystems.celestials.modules.main.guards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.grnddsystems.celestials.util.Screen

@Composable
fun AuthGuard(
    navigate: (String) -> Unit,
    init: () -> Unit = {},
    guardViewModel: GuardViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val isScreenLocked by guardViewModel.isScreenLocked.collectAsState()
    val privateKey by guardViewModel.privateKey.collectAsState()

    LaunchedEffect(Unit) {
        init()
    }

    if (privateKey != null) {
        if (isScreenLocked) {
            navigate(Screen.Lock.route)
        } else {
            content()
        }
    } else {
        // Key should have been auto-generated in initApp()
        // If somehow missing, show content anyway (key will be generated on next app launch)
        // This prevents navigation to removed Intro screen
        content()
    }
}