package com.grnddsystems.celestials.modules.main

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
import com.grnddsystems.celestials.R
import com.grnddsystems.celestials.ui.components.AppIcon
import com.grnddsystems.celestials.ui.theme.RarimeTheme
import com.grnddsystems.celestials.util.Screen

enum class BottomTab(
    val route: String,
    @DrawableRes val icon: Int,
    @DrawableRes val activeIcon: Int
) {
    Home(
        Screen.Main.Home.route,
        R.drawable.zk_home,
        R.drawable.zk_home
    ),
    Identity(
        Screen.Main.Identity.route,
        R.drawable.zk_passport_biometric,
        R.drawable.zk_passport_biometric
    ),

    QrScan(
        Screen.Main.QrScan.route,
        R.drawable.zk_qr,
        R.drawable.zk_qr
    ),
    // disabled the display of buttons on the bottom panel
//    Wallet(
//        Screen.Main.Wallet.route,
//        R.drawable.ic_wallet,
//        R.drawable.ic_wallet_filled
//    ),
    Profile(
        Screen.Main.Profile.route,
        R.drawable.zk_account,
        R.drawable.zk_account
    ),
    //added new colums on the bottom panel
//    FAQ(
//        Screen.Main.FAQ.route,
//        R.drawable.zk_frequently_asked_questions,
//        R.drawable.zk_frequently_asked_questions
//    ),
    Contact(
        "contact",
        R.drawable.zk_email,
        R.drawable.zk_email
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
                    BottomTab.QrScan -> true
                    BottomTab.Contact -> true
                    BottomTab.Identity -> true
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
private fun openEmailClient(
    context: Context,
    email: String,
    subject: String,
    body: String
) {
    // Method 1: Try direct mailto with parameters
    val mailtoUri = "mailto:$email?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}"
    val emailIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mailtoUri)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    // Method 2: Fallback to SENDTO
    val sendToIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    // Method 3: Last resort - SEND with rfc822
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        // Try METHOD 1 first (most direct)
        context.startActivity(emailIntent)
    } catch (e1: Exception) {
        try {
            // Try METHOD 2 (with chooser to exclude PayPal)
            val packageManager = context.packageManager
            val activities = packageManager.queryIntentActivities(sendToIntent, 0)

            if (activities.isNotEmpty()) {
                val chooser = Intent.createChooser(sendToIntent, "Send Email")
                context.startActivity(chooser)
            } else {
                // Try METHOD 3 as last resort
                val activities3 = packageManager.queryIntentActivities(sendIntent, 0)
                if (activities3.isNotEmpty()) {
                    val chooser = Intent.createChooser(sendIntent, "Send Email")
                    context.startActivity(chooser)
                } else {
                    Toast.makeText(context, "No email app found. Install Gmail or Outlook.", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e2: Exception) {
            Toast.makeText(context, "Failed to open email client", Toast.LENGTH_SHORT).show()
            android.util.Log.e("BottomTabBar", "All email methods failed", e2)
        }
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