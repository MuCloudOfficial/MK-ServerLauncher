package me.mucloud.application.mk.serverlauncher.dpe

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()){
            val vScrollBarState = rememberScrollState(0)
            Column(
                modifier = Modifier.verticalScroll(vScrollBarState).padding(10.dp).fillMaxSize()
            ){
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp, hoveredElevation = 10.dp),
                    modifier = Modifier.size(240.dp, 100.dp)
                ){
                    Text("Wow! Mu_Cloud Here!", Modifier.padding(10.dp).fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(vScrollBarState),
            )
        }


    }
}