package me.mucloud.application.mk.serverlauncher.dpe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import javax.swing.plaf.metal.MetalIconFactory

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MK-ServerLauncher DPE",
        state = WindowState(position = WindowPosition(Alignment.Center), width = 1024.dp, height = 768.dp),
        transparent = true,
        resizable = false,
        undecorated = true
    ) {
        Column(
            modifier = Modifier.background(Color.Gray, RoundedCornerShape(20.dp)).fillMaxSize()
        ){
            WindowDraggableArea {
                Row(
                    modifier = Modifier.background(Color.Gray, RoundedCornerShape(20.dp)).height(50.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(text = "MK-ServerLauncher DPE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = TextUnit(20.0f, TextUnitType.Sp),
                        modifier = Modifier.padding(start = 20.dp)
                    )

                }
            }
            App()
        }

    }
}