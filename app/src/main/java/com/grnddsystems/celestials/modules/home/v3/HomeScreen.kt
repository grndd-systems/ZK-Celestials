package com.grnddsystems.celestials.modules.home.v3

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grnddsystems.celestials.R
import com.grnddsystems.celestials.data.enums.AppColorScheme
import com.grnddsystems.celestials.modules.home.v3.model.WidgetType
import com.grnddsystems.celestials.modules.home.v3.ui.components.HomeHeader
import com.grnddsystems.celestials.modules.main.LocalMainViewModel
import com.grnddsystems.celestials.modules.main.ScreenInsets
import com.grnddsystems.celestials.modules.manageWidgets.ManageWidgetsBottomSheet
import com.grnddsystems.celestials.ui.components.AppBottomSheet
import com.grnddsystems.celestials.ui.components.rememberAppSheetState
import com.grnddsystems.celestials.ui.theme.RarimeTheme
import com.grnddsystems.celestials.util.ErrorHandler
import com.grnddsystems.celestials.util.PrevireSharedAnimationProvider
import com.grnddsystems.celestials.util.Screen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreenV3(
    navigate: (String) -> Unit,
    navigateWithPopUp: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    setVisibilityOfBottomBar: (Boolean) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val passport by viewModel.passport.collectAsState()
    val innerPaddings by LocalMainViewModel.current.screenInsets.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val notificationsCount by remember(notifications) {
        derivedStateOf { notifications.count { it.isActive } }
    }
    val colorScheme by viewModel.colorScheme.collectAsState()
    val visibleCards by viewModel.visibleWidgets.collectAsState()
    val isWelcomeVisible by remember {
        derivedStateOf { !viewModel.getIsShownWelcome() }
    }

    val welcomeAppSheetState = rememberAppSheetState(isWelcomeVisible)

    val sheetManageWidgets = rememberAppSheetState()
    AppBottomSheet(
        state = sheetManageWidgets,
        backgroundColor = RarimeTheme.colors.backgroundPrimary,
        isHeaderEnabled = false,
        fullScreen = false,
    ) {
        ManageWidgetsBottomSheet(onClose = { sheetManageWidgets.hide() })
    }

    HomeScreenContent(
        visibleWidgets = visibleCards,
        userPassportName = passport?.personDetails?.name,
        notificationsCount = notificationsCount,
        innerPaddings = innerPaddings,
        modifier = Modifier.fillMaxSize(),
        navigate = navigate,
        sharedTransitionScope = sharedTransitionScope,
        setVisibilityOfBottomBar = setVisibilityOfBottomBar,
        colorScheme = colorScheme,
        onClick = { sheetManageWidgets.show() },
    )

    AppBottomSheet(
        state = welcomeAppSheetState,
        isHeaderEnabled = false,
        disablePullClose = true,
        onClose = {
            viewModel.saveIsShownWelcome(true)
            welcomeAppSheetState.hide()
        }
    ) {
        WelcomeBottomSheet {
            welcomeAppSheetState.hide()
            viewModel.saveIsShownWelcome(true)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    innerPaddings: Map<ScreenInsets, Number>,
    navigate: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    setVisibilityOfBottomBar: (Boolean) -> Unit,
    visibleWidgets: List<WidgetType>,
    userPassportName: String?,
    notificationsCount: Int?,
    colorScheme: AppColorScheme,
    onClick: () -> Unit
) {
    var selectedWidgetType by remember { mutableStateOf<WidgetType?>(null) }

    LaunchedEffect(selectedWidgetType) {
        setVisibilityOfBottomBar(selectedWidgetType == null)
    }

    Box(modifier = modifier) {
        // Simple scrollable content with Celestials ID block
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPaddings[ScreenInsets.TOP]?.toFloat()?.dp ?: 0.dp,
                    bottom = innerPaddings[ScreenInsets.BOTTOM]?.toFloat()?.dp ?: 0.dp
                )
        ) {
            // Header with notifications
            HomeHeader(
                notificationsCount = notificationsCount,
                name = userPassportName,
                onNotificationClick = { navigate(Screen.NotificationsList.route) }
            )

            // Add spacing after header
            Box(modifier = Modifier.height(20.dp))

            // Celestials ID block - opens https://celestials.id/
            CelestialsIdBlock(
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            // Add spacing at bottom
            Box(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Celestials ID promotional block
 * Opens https://celestials.id/ when clicked
 */
@Composable
fun CelestialsIdBlock(
    modifier: Modifier = Modifier
) {
    // Get context to launch browser intent
    val context = LocalContext.current

    // Main column without background - only image and text card
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                // Remove ripple effect for cleaner look
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Open https://celestials.id/ in browser when clicked
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://celestials.id/"))
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Handle error if browser not available
                    ErrorHandler.logError("CelestialsIdBlock", "Failed to open URL", e)
                }
            },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Celestials banner image - NO BACKGROUND
        Image(
            painter = painterResource(id = R.drawable.celestial_banner), // Replace with your image resource
            contentDescription = "Celestials Banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp), // Increased height for better proportions
            contentScale = ContentScale.Fit // Changed to Fit to show full image without cropping
        )

        // Text card with grey background
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    RarimeTheme.colors.componentPrimary,
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            // Title section with arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Title
                    Text(
                        text = "CELESTIALS ID",
                        style = RarimeTheme.typography.h6,
                        color = RarimeTheme.colors.textPrimary
                    )

                    // Description
                    Text(
                        text = "Explore more about the next\ngeneration of identity on the web.",
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.textSecondary
                    )
                }

                // Arrow icon indicating it's clickable/external link
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right_up_line), // or ic_external_link_line
                    contentDescription = "Open external link",
                    tint = RarimeTheme.colors.textSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun HomeScreenPreview() {
    PrevireSharedAnimationProvider { sharedTransitionScope, _ ->
        Surface {
            HomeScreenContent(
                modifier = Modifier.fillMaxSize(),
                sharedTransitionScope = sharedTransitionScope,
                navigate = {},
                setVisibilityOfBottomBar = {},
                userPassportName = "Mike",
                notificationsCount = 2,
                innerPaddings = mapOf(ScreenInsets.TOP to 0, ScreenInsets.BOTTOM to 0),
                visibleWidgets = WidgetType.entries,
                colorScheme = AppColorScheme.SYSTEM,
                onClick = {}
            )
        }
    }
}