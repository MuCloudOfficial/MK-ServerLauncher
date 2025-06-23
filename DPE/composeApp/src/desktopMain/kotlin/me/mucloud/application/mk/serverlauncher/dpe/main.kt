package me.mucloud.application.mk.serverlauncher.dpe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MK-ServerLauncher DPE",
        transparent = true,
        state = WindowState(position = WindowPosition(Alignment.Center), width = 1024.dp, height = 768.dp),
        undecorated = true
    ) {
        Column(Modifier.background(Color.Gray, RoundedCornerShape(10.dp)).padding(10.dp).fillMaxSize()) {
            WindowDraggableArea(Modifier.background(Color.White, RoundedCornerShape(10.dp)).height(50.dp).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
                    Text("MK-ServerLauncher DPE", color = Color.Black,
                        fontSize = TextUnit(24.0f, TextUnitType.Sp),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }
            }
            Spacer(Modifier.height(10.dp).fillMaxWidth())
            Box(Modifier.background(Color.White, RoundedCornerShape(10.dp)).fillMaxSize()){
                App()
            }
        }
    }
}