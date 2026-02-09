package com.grnddsystems.celestials.modules.home.components.no_passport.specific

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grnddsystems.celestials.R
import com.grnddsystems.celestials.modules.home.components.HomeIntroLayout
import com.grnddsystems.celestials.ui.base.ButtonSize
import com.grnddsystems.celestials.ui.components.HorizontalDivider
import com.grnddsystems.celestials.ui.components.PrimaryButton
import com.grnddsystems.celestials.ui.components.UiPrivacyCheckbox
import com.grnddsystems.celestials.ui.components.rememberAppCheckboxState
import com.grnddsystems.celestials.ui.theme.RarimeTheme

@Composable
fun AirdropIntroScreen(onStart: () -> Unit) {
    val termsAcceptedState = rememberAppCheckboxState()

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxHeight()
            .padding(bottom = 20.dp)
    ) {
        HomeIntroLayout(
            icon = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(72.dp)
                        .height(72.dp)
                        .background(RarimeTheme.colors.componentPrimary, CircleShape)
                ) {
                    Text(
                        text = "🇺🇦",
                        style = RarimeTheme.typography.h5,
                        color = RarimeTheme.colors.textPrimary,
                    )
                }
            },
            title = stringResource(R.string.airdrop_intro_title),
            description = stringResource(R.string.airdrop_intro_description)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.airdrop_intro_text_title),
                    style = RarimeTheme.typography.overline2,
                    color = RarimeTheme.colors.textSecondary
                )
                Text(
                    text = stringResource(R.string.airdrop_intro_text),
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textPrimary
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            HorizontalDivider()
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                UiPrivacyCheckbox(termsAcceptedState = termsAcceptedState)
                PrimaryButton(
                    text = stringResource(R.string.continue_btn),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = termsAcceptedState.checked,
                    size = ButtonSize.Large,
                    onClick = onStart
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AirdropIntroScreenPreview() {
    AirdropIntroScreen(onStart = {})
}