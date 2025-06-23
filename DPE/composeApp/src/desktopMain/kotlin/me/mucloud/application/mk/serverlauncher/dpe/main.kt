package me.mucloud.application.mk.serverlauncher.dpe

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DPE",
    ) {
        App()
    }
}