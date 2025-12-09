package com.rarilabs.rarime.modules.main


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.qr.ScanQrScreen
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppLogo
import com.rarilabs.rarime.ui.components.UiSnackbarDefault
import com.rarilabs.rarime.ui.components.enter_program.EnterProgramFlow
import com.rarilabs.rarime.ui.components.enter_program.UNSPECIFIED_PASSPORT_STEPS
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.AppTheme
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Screen

val mainRoutes = listOf(
    Screen.Main.Home.route,
    Screen.Main.Rewards.RewardsMain.route,
    Screen.Main.Wallet.route,
    Screen.Main.Profile.route,
    Screen.Main.Identity.route
)

val LocalMainViewModel = compositionLocalOf<MainViewModel> { error("No MainViewModel provided") }

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController
) {

    LaunchedEffect(Unit) {
        mainViewModel.initApp()
    }


    CompositionLocalProvider(LocalMainViewModel provides mainViewModel) {
        MainScreenContent(navController = navController)
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            // added text to main screen
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
              AppLogo()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 32.dp)
                ) {
                    Spacer(modifier = Modifier.height(35.dp))
                    Text(
                        text = "Welcome to",
                        color = RarimeTheme.colors.primaryDarker,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(35.dp))
                    Text(
                        text = "ZK-KYC",
                        color = RarimeTheme.colors.primaryDarker,
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.sudo)),
                            fontSize = 48.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        ),
                    )
                    Text(
                        text = "celestials id",
                        color = RarimeTheme.colors.primaryDarker,
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.sudo)),
                            fontSize = 48.sp,
                            letterSpacing = 5.sp,
                            lineHeight = 55.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        ),
                    )
                }
            }

            // reason: Add "Based on Rarime" text at bottom with safe area padding
            Text(
                text = "Based on Rarime",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .navigationBarsPadding()
            )
        }
    }
}

@Composable
fun AppLoadingScreen() {
    SplashScreen()
}

@Composable
fun AppLoadingFailedScreen() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        AppIcon(id = R.drawable.ic_warning, tint = RarimeTheme.colors.errorDark, size = 140.dp)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreenContent(
    navController: NavHostController,
) {
    val mainViewModel = LocalMainViewModel.current
    val context = LocalContext.current


    val passportStatus by mainViewModel.passportStatus.collectAsState()
    val isModalShown by mainViewModel.isModalShown.collectAsState()
    val modalContent by mainViewModel.modalContent.collectAsState()
    val pointsToken by mainViewModel.pointsToken.collectAsState()

    val snackbarHostState by mainViewModel.snackbarHostState.collectAsState()
    val snackbarContent by mainViewModel.snackbarContent.collectAsState()

    val colorSchema by mainViewModel.colorScheme.collectAsState()

    val enterProgramSheetState = rememberAppSheetState()
    val qrCodeState = rememberAppSheetState()
    val isBottomBarShown by mainViewModel.isBottomBarShown.collectAsState()
    // Use remember to cache navBackStackEntry and currentRoute
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute by remember(navBackStackEntry) {
        derivedStateOf { navBackStackEntry?.destination?.route }
    }

    // Compute shouldShowBottomBar based on currentRoute
    val shouldShowBottomBar by remember(currentRoute) {
        derivedStateOf { currentRoute != null && currentRoute in mainRoutes }
    }

    // Use LaunchedEffect to update bottom bar visibility when shouldShowBottomBar changes
    LaunchedEffect(shouldShowBottomBar) {
        mainViewModel.setBottomBarVisibility(shouldShowBottomBar)
    }

    // Use rememberUpdatedState for pointsToken and enterProgramSheetState
    val pointsTokenState = rememberUpdatedState(pointsToken)
    val enterProgramSheetStateState = rememberUpdatedState(enterProgramSheetState)
    val qrCodeSheetState = rememberUpdatedState(qrCodeState)

    // Define navigation functions using remember to prevent recomposition
    val simpleNavigate = remember(navController) {
        { route: String ->
            navController.navigate(route)
        }
    }

    val navigateWithPopUp = remember(navController) {
        { route: String ->
            val currentPointsToken = pointsTokenState.value
            val currentEnterProgramSheetState = enterProgramSheetStateState.value

            Log.d("URL string", route)
            if (route == Screen.Main.Rewards.RewardsMain.route) {
                if (currentPointsToken?.balanceDetails?.attributes == null) {
                    currentEnterProgramSheetState.show()
                } else {
                    navController.navigate(route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            } else if (route == Screen.Main.QrScan.route) {
                val currentQrCodeSheetState = qrCodeSheetState.value
                currentQrCodeSheetState.show()
            } else {
                navController.navigate(route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    restoreState = true
                    launchSingleTop = true
                }
            }
        }
    }

    AppTheme(colorScheme = colorSchema) {
        Scaffold(
            containerColor = RarimeTheme.colors.backgroundPrimary,
            bottomBar = {
                if (isBottomBarShown) {
                    BottomTabBar(
                        modifier = Modifier.navigationBarsPadding(),
                        currentRoute = currentRoute,
                        onRouteSelected = { navigateWithPopUp(it) },
                        onQrCodeRouteSelected = { mainViewModel })
                }
            },

            snackbarHost = {
                // Show custom snackbar instead of `SnackbarHost`
                snackbarContent?.let { snackContent ->
                    Column(
                        modifier = Modifier
                            .zIndex(100f)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        UiSnackbarDefault(snackContent)

                        // Disappear automatically
                        LaunchedEffect(snackContent) {
                            kotlinx.coroutines.delay(
                                when (snackContent.duration) {
                                    SnackbarDuration.Short -> 2000
                                    SnackbarDuration.Long -> 4000
                                    SnackbarDuration.Indefinite -> Long.MAX_VALUE
                                }
                            )
                            mainViewModel.clearSnackbarOptions()
                        }
                    }
                }
            },
        ) { innerPaddings ->
            mainViewModel.setScreenInsets(
                top = innerPaddings.calculateTopPadding().value,
                bottom = innerPaddings.calculateBottomPadding().value
            )

            ScreenBarsColor(
                colorScheme = colorSchema, route = currentRoute ?: ""
            )


            MainScreenRoutes(
                navController = navController,
                simpleNavigate = { simpleNavigate(it) },
                navigateWithPopUp = { navigateWithPopUp(it) },
            )

            if (isModalShown) {
                Dialog(onDismissRequest = { mainViewModel.setModalVisibility(false) }) {
                    modalContent()
                }
            }

            AppBottomSheet(
                state = qrCodeState, fullScreen = true, isHeaderEnabled = false
            ) {
                ScanQrScreen(onBack = {
                    qrCodeState.hide()
                }, onScan = {
                    val uri = it.toUri()
                    qrCodeState.hide()
                    mainViewModel.setExtIntDataURI(uri)
                })
            }

            AppBottomSheet(
                state = enterProgramSheetState,
                fullScreen = true,
                isHeaderEnabled = false,
            ) { hide ->
                EnterProgramFlow(
                    initialStep = UNSPECIFIED_PASSPORT_STEPS.ONLY_INVITATION,
                    onFinish = {
                        hide {
                            navController.navigate(Screen.Main.Rewards.RewardsMain.route)
                        }
                    },
                    sheetState = enterProgramSheetState,
                    hide = hide,
                    passportStatus = passportStatus,
                )
            }
        }
    }


}


@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}