package com.grnddsystems.celestials.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.grnddsystems.celestials.R

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


