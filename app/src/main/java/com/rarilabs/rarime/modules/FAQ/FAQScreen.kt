// FAQ/FAQScreen.kt
// file to generate new window in app to check faq
package com.rarilabs.rarime.modules.faq

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
import com.rarilabs.rarime.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FAQ") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Main.Home.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // FAQ контент
            FAQItem(
                question = "What is ZK Celestials ID?",
                answer = "ZK Celestials ID is a decentralized identity solution based on zero-knowledge proofs..."
            )

            Spacer(modifier = Modifier.height(16.dp))

            FAQItem(
                question = "How to use my wallet key?",
                answer = "Your Celestials ID is your private key (40 characters). Keep it safe and never share it..."
            )

            Spacer(modifier = Modifier.height(16.dp))

            FAQItem(
                question = "Is my data secure?",
                answer = "Yes, all your data is encrypted and stored locally on your device..."
            )

            // Додайте більше питань тут
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = answer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}