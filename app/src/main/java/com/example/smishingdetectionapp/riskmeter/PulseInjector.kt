package com.example.smishingdetectionapp.riskmeter

import androidx.compose.ui.platform.ComposeView

fun injectPulsing(view: ComposeView) {
    view.setContent {
        Pulsing()
    }
}
