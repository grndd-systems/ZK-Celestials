package com.rarilabs.celestial.modules.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.celestial.R
import com.rarilabs.celestial.ui.components.AppSheetState
import com.rarilabs.celestial.ui.components.rememberAppSheetState
import com.rarilabs.celestial.ui.theme.RarimeTheme
import kotlinx.coroutines.delay

enum class QrProofStep(val step: Int) {
    PREPARING(0),
    GENERATING(1),
    FINALIZING(2),
    COMPLETED(3)
}

val qrProofStepDescription = mapOf(
    QrProofStep.PREPARING to "Preparing proof generation",
    QrProofStep.GENERATING to "Generating zero-knowledge proof",
    QrProofStep.FINALIZING to "Finalizing proof",
    QrProofStep.COMPLETED to "Proof generated successfully"
)

@Composable
fun QrProofProgressSheet(
    modifier: Modifier = Modifier,
    proofSheetState: AppSheetState,
    onComplete: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var currentStepIndex by remember { mutableFloatStateOf(0f) }
    val totalSteps = QrProofStep.entries.size - 1 // Exclude COMPLETED

    // Smooth progress animation (15 seconds total)
    LaunchedEffect(Unit) {
        val totalDuration = 15000L // 15 seconds
        val updateInterval = 50L // Update every 50ms for smooth animation
        val totalUpdates = totalDuration / updateInterval
        val progressIncrement = 1f / totalUpdates

        repeat(totalUpdates.toInt()) {
            delay(updateInterval)
            progress += progressIncrement
            currentStepIndex = (progress * totalSteps).coerceIn(0f, totalSteps.toFloat())
        }

        // Ensure completion
        progress = 1f
        currentStepIndex = totalSteps.toFloat()
        delay(500) // Brief pause

        // Auto-dismiss
        proofSheetState.hide()
        onComplete()
    }

    // Prevent manual dismissal
    LaunchedEffect(proofSheetState.showSheet) {
        if (!proofSheetState.showSheet && progress < 1f) {
            proofSheetState.show()
        }
    }

    Surface(
        modifier = modifier,
        color = RarimeTheme.colors.backgroundPrimary
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Applying QR",
                style = RarimeTheme.typography.h3,
                color = RarimeTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Please don't close the application",
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.errorMain,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Current step description
            val currentStepEnum = QrProofStep.entries[currentStepIndex.toInt().coerceIn(0, totalSteps)]
            Text(
                text = qrProofStepDescription[currentStepEnum] ?: "",
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Horizontal progress slider (like passport scan)
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = RarimeTheme.colors.primaryMain,
                trackColor = RarimeTheme.colors.componentDisabled,
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview
@Composable
private fun QrProofProgressSheetPreview() {
    QrProofProgressSheet(
        proofSheetState = rememberAppSheetState(),
        onComplete = {}
    )
}