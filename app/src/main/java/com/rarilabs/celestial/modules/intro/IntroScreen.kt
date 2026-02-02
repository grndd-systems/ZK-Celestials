package com.rarilabs.celestial.modules.intro

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.celestial.R
import com.rarilabs.celestial.ui.components.AppLogo
import com.rarilabs.celestial.ui.components.PrimaryButton
import com.rarilabs.celestial.ui.theme.RarimeTheme

private enum class IntroStep(
    @StringRes val title: Int,
    @StringRes val text: Int,
    @RawRes val animation: Int,
    val animationWidth: Int,
) {
    Welcome(
        title = R.string.intro_step_1_title,
        text = R.string.intro_step_1_text,
        animation = R.raw.anim_intro_welcome,
        animationWidth = 390
    ),
    Identity(
        title = R.string.intro_step_2_title,
        text = R.string.intro_step_2_text,
        animation = R.raw.anim_intro_incognito,
        animationWidth = 342
    ),
    Privacy(
        title = R.string.intro_step_3_title,
        text = R.string.intro_step_3_text,
        animation = R.raw.anim_intro_proofs,
        animationWidth = 320
    ),
    Rewards(
        title = R.string.intro_step_4_title,
        text = R.string.intro_step_4_text,
        animation = R.raw.anim_intro_rewards,
        animationWidth = 220
    )
}

private enum class OnboardingStep {
    INTRO
}

/**
 * @deprecated IntroScreen is no longer used - private keys are now auto-generated on first launch.
 * This screen is kept for reference but is not part of the navigation flow.
 * IntroLegalScreen below is still actively used in the Identity flow.
 */
@Deprecated("Intro screens removed - keys are auto-generated", level = DeprecationLevel.HIDDEN)
@Composable
fun IntroScreen(
    onFinish: (String) -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: IntroViewModel = hiltViewModel()
) {
    IntroScreenContent(
        onFinish = onFinish,
        viewModel = viewModel,
        navigate = onNavigate
    )
}

@Composable
fun IntroScreenContent(
    navigate: (String) -> Unit,
    viewModel: IntroViewModel,
    onFinish: (String) -> Unit
) {
    var currentStep by remember { mutableStateOf(OnboardingStep.INTRO) }
    val coroutineScope = rememberCoroutineScope()

    val introSteps = rememberSaveable {
        listOf(
            IntroStep.Welcome, IntroStep.Identity, IntroStep.Privacy, IntroStep.Rewards
        )
    }
    val stepState = rememberPagerState(pageCount = { 1 })

    when (currentStep) {
        OnboardingStep.INTRO -> {
            Column(
                verticalArrangement = Arrangement.spacedBy(48.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .background(RarimeTheme.colors.backgroundPrimary)
                    .padding(bottom = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    HorizontalPager(
                        state = stepState,
                        verticalAlignment = Alignment.Top,
                    ) { page ->
                        StepView(introSteps[page])
                    }
                }
                Column(
                    modifier = Modifier.padding(horizontal = 48.dp),
                    verticalArrangement = Arrangement.spacedBy(48.dp)
                ) {
                    // Simple button without icon - just centered text
                    var isLoading by remember { mutableStateOf(false) }
                    
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = if (isLoading) "Generating..." else "Start",
                        enabled = !isLoading,
                        onClick = {
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    // Generate new private key automatically
                                    viewModel.generateAndSavePrivateKey()
                                    
                                    // Proceed to main screen
                                    onFinish(Screen.Main.Home.route)
                                } catch (e: Exception) {
                                    isLoading = false
                                    // Error will be logged in ViewModel
                                    // In a production app, you might want to show an error dialog here
                                }
                            }
                        }
                    )
                    
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = RarimeTheme.colors.primaryMain
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
internal fun IntroLegalScreen(
    url: String,
    title: String,
    onAgree: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                            }
                        }
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(RarimeTheme.colors.backgroundPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = RarimeTheme.colors.textPrimary
                        )
                        Text(
                            text = "Loading $title...",
                            style = RarimeTheme.typography.body4,
                            color = RarimeTheme.colors.textSecondary
                        )
                    }
                }
            }
        }

        // Bottom button area with navigation bar insets to ensure button is always visible
        // and tappable above system navigation controls on all devices and orientations
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // Ensures button is above system navigation bar
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 20.dp)
        ) {
            PrimaryButton(
                text = "I have read and agree to the rules",
                onClick = onAgree,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StepView(step: IntroStep) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            ) {
                AppLogo()
                Spacer(modifier = Modifier.height(35.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(step.text),
                        style = RarimeTheme.typography.subtitle4,
                        lineHeight = 80.sp,
                        color = RarimeTheme.colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(35.dp))
                    Text(
                        text = stringResource(step.title),
                        style = if (step == IntroStep.Welcome) {
                            TextStyle(
                                fontFamily = FontFamily(Font(R.font.sudo)),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 5.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 55.sp,
                            )
                        } else {
                            RarimeTheme.typography.h1
                        },
                        color = RarimeTheme.colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}