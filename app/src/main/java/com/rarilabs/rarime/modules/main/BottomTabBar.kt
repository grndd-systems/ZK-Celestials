package com.rarilabs.rarime.modules.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.BuildConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Screen

enum class BottomTab(
    val route: String,
    @DrawableRes val icon: Int,
    @DrawableRes val activeIcon: Int
) {
    Home(
        Screen.Main.Home.route,
        R.drawable.ic_house_simple,
        R.drawable.ic_house_simple_fill
    ),
    // disabled the display of buttons on the bottom panel
//    Identity(
//        Screen.Main.Identity.route,
//        R.drawable.ic_passport,
//        R.drawable.ic_passport_fill
//    ),
//    QrScan(
//        Screen.Main.QrScan.route,
//        R.drawable.ic_qr_scan,
//        R.drawable.ic_qr_scan
//    ),
//
//    Wallet(
//        Screen.Main.Wallet.route,
//        R.drawable.ic_wallet,
//        R.drawable.ic_wallet_filled
//    ),
    Profile(
        Screen.Main.Profile.route,
        R.drawable.ic_user,
        R.drawable.ic_user_fill
    ),
    //added new colums on the bottom panel
    FAQ(
        Screen.Main.FAQ.route,
        R.drawable.ic_question_line,
        R.drawable.ic_question
    ),
    Contact(
        "contact",
        R.drawable.ic_airdrop,
        R.drawable.ic_airdrop
    ),
    // disabled the display of buttons on the bottom panel
//    Debug(
//        Screen.Main.DebugIdentity.route,
//        R.drawable.welcome_cat,
//        R.drawable.welcome_cat
//    )
}

@Composable
fun BottomTabBar(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    onRouteSelected: (String) -> Unit,
    onQrCodeRouteSelected: (String) -> Unit
) {
    val context = LocalContext.current  // added context to fix the bug
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(RarimeTheme.colors.backgroundPrimary)
                .padding(vertical = 12.dp)
        ) {
            // turned on visibility of button in the Bottom panel
            BottomTab.entries.forEach { tab ->
                val shouldDraw = when (tab) {
                    BottomTab.Home -> true
                    BottomTab.Profile -> true
                    BottomTab.FAQ -> true
                    BottomTab.Contact -> true
                    else -> false
                }

                if (shouldDraw) {
                    TabItem(
                        tab = tab,
                        isSelected = currentRoute == tab.route,
                        onTabSelected = { onRouteSelected(it.route) },
                        onQrCodeRouteSelected = { onQrCodeRouteSelected(it.route) },
                        context = context
                    )
                }
            }
        }
    }
}

@Composable
private fun TabItem(
    tab: BottomTab,
    isSelected: Boolean,
    onTabSelected: (BottomTab) -> Unit,
    onQrCodeRouteSelected: (BottomTab) -> Unit,
    context: Context
) {
    val animatedColor by animateColorAsState(
        if (isSelected) RarimeTheme.colors.componentPrimary else Color.Transparent,
        label = "animated_tab_bar_bg"
    )

    Column(
        modifier = Modifier
            .width(48.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(animatedColor)
            .pointerInput(Unit) {
                detectTapGestures {
                    // Fixed Contact Us button action to open correct screen
                    if (tab == BottomTab.Contact) {
                        openEmailClient(
                            context = context,
                            email = "help@grndd.systems",
                            subject = "ZK Celestials ID Support",
                            body = "Hello,\n\n"
                        )
                    } else {
                        if (tab.route == Screen.Main.QrScan.route) {
                            onQrCodeRouteSelected(tab)
                        }
                        onTabSelected(tab)
                    }
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AppIcon(
            id = if (isSelected) tab.activeIcon else tab.icon,
            tint = if (isSelected) RarimeTheme.colors.textPrimary else RarimeTheme.colors.textPlaceholder,
        )
    }
}

// added function to open to send email
private fun openEmailClient(
    context: Context,
    email: String,
    subject: String,
    body: String
) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Send Email"))
    } catch (e: Exception) {
        Toast.makeText(context, "No email client found", Toast.LENGTH_SHORT).show()
    }
}
@Preview
@Composable
private fun BottomTabBarPreview() {
    var selectedTab by remember { mutableStateOf(BottomTab.Home) }
    BottomTabBar(
        currentRoute = selectedTab.route,
        onRouteSelected = { route -> selectedTab = BottomTab.entries.first { it.route == route } }
    ) {}
}