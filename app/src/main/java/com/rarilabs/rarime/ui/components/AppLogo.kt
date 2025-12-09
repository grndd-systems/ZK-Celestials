package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    //changed iconsize on the start screen
    scale: Float = 1.5f,
    wrapperSize: Int = 250,
    iconSize: Int = 150
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(wrapperSize.dp)
    ) {
        //changed app logo on the start screen
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.celestial_no_backgraund),
            contentDescription = "App logo",
            modifier = Modifier
                .size(iconSize.dp)
                .scale(scale)
        )
    }
}


