package com.example.simon

import android.content.res.Configuration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val simonButtons = listOf(
    Color.Red to "R",
    Color.Green to "G",
    Color.Blue to "B",
    Color.Yellow to "Y",
    Color.Cyan to "C",
    Color.Magenta to "M",
)

@Composable
fun Screen1(onRecap: (String) -> Unit) {

    val sequence = rememberSaveable { mutableStateListOf<String>() }
    val scrollState = rememberScrollState()
    // telefono orizzontale
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // scroll automatico quando riempio il box
    LaunchedEffect(sequence.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    if (isLandscape) {
        LandscapeLayout(sequence, scrollState, onRecap)
    } else {
        PortraitLayout(sequence, scrollState, onRecap)
    }
}

@Composable
private fun LandscapeLayout(
    sequence: MutableList<String>,
    scrollState: ScrollState,
    onRecap: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // griglia bottoni a sinistra
        BoxWithConstraints(
            modifier = Modifier
                .weight(0.8f)
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            val sp = 8.dp
            // calcolo altezza
            val dynHeight = (this.maxHeight - sp * 2) / 3

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(sp),
                horizontalArrangement = Arrangement.spacedBy(sp),
            ) {
                items(simonButtons) { (color, letter) ->
                    Button(
                        onClick = { sequence.add(letter) },
                        colors = ButtonDefaults.buttonColors(containerColor = color),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dynHeight),
                        shape = RectangleShape
                    ) {}
                }
            }
        }

        // parte destra: sequenza + bottoni
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            SequenceBox(sequence, scrollState, Modifier.weight(1f).padding(bottom = 15.dp))
            ActionButtons(sequence, onRecap, height = 45)
        }
    }
}

@Composable
private fun PortraitLayout(
    sequence: MutableList<String>,
    scrollState: ScrollState,
    onRecap: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // griglia di bottoni in alto
        BoxWithConstraints(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            val sp = 12.dp
            // calcolo altezza
            val dynHeight = (this.maxHeight - sp * 2) / 3

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(sp),
                horizontalArrangement = Arrangement.spacedBy(sp)
            ) {
                items(simonButtons) { (color, letter) ->
                    Button(
                        onClick = { sequence.add(letter) },
                        colors = ButtonDefaults.buttonColors(containerColor = color),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dynHeight),
                        shape = RectangleShape
                    ) {}
                }
            }
        }

        SequenceBox(
            sequence, scrollState,
            Modifier
                .weight(0.6f)
                .padding(vertical = 24.dp),
        )

        ActionButtons(sequence, onRecap, height = 50)
    }
}

@Composable
private fun SequenceBox(
    sequence: List<String>,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp),
    ) {
        // sequenza premuta
        Text(
            text = sequence.joinToString(", "),
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.verticalScroll(scrollState)
        )
    }
}

@Composable
private fun ActionButtons(
    sequence: MutableList<String>,
    onRecap: (String) -> Unit,
    height: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // clear
        Button(
            onClick = { sequence.clear() },
            modifier = Modifier
                .weight(1f)
                .height(height.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
            Text(stringResource(R.string.clear), fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        // fine sequenza
        Button(
            onClick = {
                val res = sequence.joinToString(",")
                sequence.clear()
                onRecap(res)
            },
            modifier = Modifier
                .weight(1f)
                .height(height.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
            Text(stringResource(R.string.end), fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}