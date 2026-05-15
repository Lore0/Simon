package com.example.simon

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
fun GameScreen(
    viewModel: GameViewModel,
    onRecap: () -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    LaunchedEffect(viewModel.playerSeq.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    BackHandler {
        viewModel.handleGameOver(
            errorIndex = viewModel.playerSeq.size
        )
        onRecap()
    }

    if (isLandscape) {
        LandscapeLayout(viewModel, scrollState, onRecap)
    } else {
        PortraitLayout(viewModel, scrollState, onRecap)
    }
}

@Composable
private fun LandscapeLayout(
    viewModel: GameViewModel,
    scrollState: ScrollState,
    onRecap: () -> Unit
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
                .weight(0.9f)
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            ColorGrid(viewModel = viewModel, maxHeight = this.maxHeight)
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
            SequenceBox(
                state = viewModel.state,
                playerSeq = viewModel.playerSeq,
                scrollState = scrollState,
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
            )
            ActionButtons(viewModel, height = 60, onRecap)
        }
    }
}

@Composable
private fun PortraitLayout(
    viewModel: GameViewModel,
    scrollState: ScrollState,
    onRecap: () -> Unit
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
            ColorGrid(viewModel = viewModel, maxHeight = this.maxHeight)
        }
        SequenceBox(
            state = viewModel.state,
            playerSeq = viewModel.playerSeq,
            scrollState = scrollState,
            modifier = Modifier
                .weight(0.3f)
                .padding(vertical = 12.dp)
        )
        // Bottoni in basso
        ActionButtons(viewModel, height = 55, onRecap)
    }
}

@Composable
private fun ColorGrid(viewModel: GameViewModel, maxHeight: androidx.compose.ui.unit.Dp) {
    val sp = 12.dp
    // calcolo altezza
    val dynHeight = (maxHeight - sp * 2) / 3

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(sp),
        horizontalArrangement = Arrangement.spacedBy(sp)
    ) {
        items(simonButtons) { (color, letter) ->
            val isComputerTurn = viewModel.state == GameState.COMP_TURN || viewModel.state == GameState.PAUSED
            val shine = viewModel.activeColor == letter

            // si scurisce (alpha 0.3) se COMP_TURN e non è activeColro
            val displayColor = if (isComputerTurn && !shine) color.copy(alpha = 0.3f) else color

            Button(
                onClick = { viewModel.onPlayerTurn(letter) },
                enabled = viewModel.state == GameState.PLAYER_TURN,
                colors = ButtonDefaults.buttonColors(
                    containerColor = displayColor,
                    disabledContainerColor = displayColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dynHeight),
                shape = RectangleShape
            ) {}
        }
    }
}

@Composable
private fun SequenceBox(
    state: GameState,
    playerSeq: List<String>,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.TopStart
    ) {
        val isErrorState = state == GameState.GAME_OVER

        val displayText = if (isErrorState) {
            stringResource(R.string.error_message)
        } else if (state == GameState.PLAYER_TURN) {
            playerSeq.joinToString(", ")
        } else if (state == GameState.PAUSED) {
            stringResource(R.string.pause)
        } else {
            ""
        }

        val textColor = if (isErrorState) Color.Red else Color.Black

        Text(
            text = displayText,
            fontSize = 18.sp,
            color = textColor,
            fontWeight = if (isErrorState) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.verticalScroll(scrollState)
        )
    }
}

@Composable
private fun ActionButtons(
    viewModel: GameViewModel,
    height: Int,
    onRecap: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // avvia
        Button(
            onClick = { viewModel.startGame() },
            enabled = viewModel.state == GameState.READY,
            modifier = Modifier.weight(1f).height(height.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.start), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        // pausa / riprendi
        Button(
            onClick = { viewModel.onPause() },
            enabled = viewModel.state == GameState.COMP_TURN || viewModel.state == GameState.PAUSED,
            modifier = Modifier.weight(1f).height(height.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            val pauseText = if (viewModel.state == GameState.PAUSED) {
                stringResource(R.string.resume)
            } else {
                stringResource(R.string.pause)
            }
            Text(pauseText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        // fine partita
        Button(
            onClick = {
                viewModel.handleGameOver(
                    errorIndex = viewModel.playerSeq.size
                )
                onRecap()
            },
            enabled = viewModel.state != GameState.READY && viewModel.state != GameState.GAME_OVER,
            modifier = Modifier.weight(1f).height(height.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.end), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}