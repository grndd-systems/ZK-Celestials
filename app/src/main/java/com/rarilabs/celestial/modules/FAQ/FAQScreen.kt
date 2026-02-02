// FAQ/FAQScreen.kt
// file to generate new window in app to check faq
package com.rarilabs.celestial.modules.faq

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rarilabs.celestial.ui.theme.RarimeTheme
import com.rarilabs.celestial.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(navController: NavController) {
    Scaffold(
        containerColor = RarimeTheme.colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "FAQ",
                        color = RarimeTheme.colors.textPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RarimeTheme.colors.backgroundPrimary,
                    titleContentColor = RarimeTheme.colors.textPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Main.Home.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = RarimeTheme.colors.textPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RarimeTheme.colors.backgroundPrimary)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // FAQ content
            FAQItem(
                question = "What does the QR code contain?",
                answer = "The QR code contains only your Celestial ID number — nothing else. It does not store personal data, passport details, or any sensitive information."
            )

            Spacer(modifier = Modifier.height(16.dp))

            FAQItem(
                question = "What happens when I scan the QR code?",
                answer = "Your phone receives the Celestial ID from the QR code and uses it to link the verification process to your account. The QR itself does not provide any personal information."
            )

            Spacer(modifier = Modifier.height(16.dp))

            FAQItem(
                question = "How is my passport processed during verification?",
                answer = "Your passport is scanned locally on your device. The data never leaves your phone. It’s used only to generate a zero-knowledge proof confirming that your passport is valid."
            )
            Spacer(modifier = Modifier.height(16.dp))

            FAQItem(
                question = "Is any passport information stored?",
                answer = "No. Passport data is never uploaded, never stored, and never shared. Only the zero-knowledge proof — a mathematical confirmation — is sent to the network."
            )
            Spacer(modifier = Modifier.height(16.dp))

            FAQItem(
                question = "Can someone misuse my QR code?",
                answer = "No. The QR code only contains an ID number, not your personal info. Without your wallet and your device, no one can generate proofs on your behalf."
            )
            Spacer(modifier = Modifier.height(16.dp))

            FAQItem(
                question = "What happens if the proof submission fails?",
                answer = "You can try again. Since all data stays on your device until the proof is sent, nothing is lost and no personal information is leaked."
            )
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = RarimeTheme.colors.componentPrimary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = question,
                style = RarimeTheme.typography.buttonLarge,
                color = RarimeTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = answer,
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.textSecondary
            )
        }
    }
}