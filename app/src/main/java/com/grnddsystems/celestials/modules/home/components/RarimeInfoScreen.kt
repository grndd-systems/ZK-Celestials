package com.grnddsystems.celestials.modules.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grnddsystems.celestials.R
import com.grnddsystems.celestials.ui.base.ButtonSize
import com.grnddsystems.celestials.ui.components.AppIcon
import com.grnddsystems.celestials.ui.components.PrimaryButton
import com.grnddsystems.celestials.ui.theme.RarimeTheme

@Composable
fun RarimeInfoScreen(onClose: () -> Unit) {
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
                AppIcon(
                    id = R.drawable.ic_rarime,
                    size = 32.dp,
                    tint = RarimeTheme.colors.textPrimary
                )
            }
        },
        title = stringResource(R.string.Celestial)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DescriptionStepRow(text = stringResource(R.string.Celestial))
                DescriptionStepRow(text = stringResource(R.string.Celestial))
                DescriptionStepRow(annotatedText = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = RarimeTheme.colors.textPrimary
                        )
                    ) {
                        append(stringResource(R.string.Celestial))
                    }
                    append(stringResource(R.string.Celestial))
                })
            }
            PrimaryButton(
                text = stringResource(R.string.okay_btn),
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                onClick = onClose
            )
        }
    }
}

@Composable
private fun DescriptionStepRow(text: String? = null, annotatedText: AnnotatedString? = null) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "•",
            style = RarimeTheme.typography.subtitle3,
            color = RarimeTheme.colors.textSecondary,
        )
        text?.let {
            Text(
                text = it,
                style = RarimeTheme.typography.body2,
                color = RarimeTheme.colors.textSecondary,
            )
        }
        annotatedText?.let {
            Text(
                text = it,
                style = RarimeTheme.typography.body2,
                color = RarimeTheme.colors.textSecondary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RarimeInfoScreenPreview() {
    RarimeInfoScreen(onClose = {})
}