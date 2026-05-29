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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.simon.viewmodel.GameState
import com.example.simon.viewmodel.GameViewModel

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
        if (!viewModel.actualError)
            viewModel.handleGameOver(errorIndex = viewModel.playerSeq.size, isActualError = false)
        onRecap()
    }

    // quando app in va in background continua a mostrare seq se in comp_turn
    // stoppiamo manualmente -> mettiamo in pausa
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        if (viewModel.state == GameState.COMP_TURN) {
            viewModel.onPause()
        }
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
            ColorGrid(viewModel, this.maxHeight)
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
                viewModel = viewModel,
                scrollState = scrollState,
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
            )
            ActionButtons(viewModel, height = 60.dp, onRecap)
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
            ColorGrid(viewModel, this.maxHeight)
        }
        SequenceBox(
            viewModel = viewModel,
            scrollState = scrollState,
            modifier = Modifier
                .weight(0.3f)
                .padding(vertical = 12.dp)
        )
        // Bottoni in basso
        ActionButtons(viewModel, height = 55.dp, onRecap)
    }
}

@Composable
private fun ColorGrid(viewModel: GameViewModel, maxHeight: Dp) {
    val sp = 12.dp
    // calcolo altezza
    val dynHeight = (maxHeight - sp * 2) / 3
    val isComputerTurn = viewModel.state == GameState.COMP_TURN || viewModel.state == GameState.PAUSED

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(sp),
        horizontalArrangement = Arrangement.spacedBy(sp)
    ) {
        items(simonButtons) { (color, letter) ->
            val isActive = viewModel.activeColor == letter || viewModel.flashAll
            val alpha = if (isActive) 1.0f else if (isComputerTurn) 0.35f else 0.6f
            val displayColor = color.copy(alpha = alpha)

            Button(
                onClick = { viewModel.onPlayerTurn(letter) },
                enabled = viewModel.state == GameState.PLAYER_TURN && !viewModel.flashAll,
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
    viewModel: GameViewModel,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val isError = viewModel.actualError
    val displayText = if (isError) {
        stringResource(R.string.error_message)
    } else if (viewModel.state == GameState.PLAYER_TURN) {
        viewModel.playerSeq.joinToString(", ")
    } else if (viewModel.state == GameState.PAUSED) {
        stringResource(R.string.pause)
    } else {
        ""
    }
    val textColor = if (isError) Color.Red else Color.Black

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Text(
            text = displayText,
            fontSize = 18.sp,
            color = textColor,
            fontWeight = if (isError) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.verticalScroll(scrollState)
        )
    }
}

@Composable
private fun ActionButtons(
    viewModel: GameViewModel,
    height: Dp,
    onRecap: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val btnModifier = Modifier.weight(1f).height(height)
        val btnShape = RoundedCornerShape(16.dp)

        // avvia
        Button(
            onClick = { viewModel.startGame() },
            enabled = viewModel.state == GameState.READY,
            modifier = btnModifier,
            shape = btnShape
        ) {
            Text(stringResource(R.string.start), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        // pausa / riprendi
        Button(
            onClick = { viewModel.onPause() },
            enabled = viewModel.state == GameState.COMP_TURN || viewModel.state == GameState.PAUSED,
            modifier = btnModifier,
            shape = btnShape
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
                if (!viewModel.actualError) {
                    viewModel.handleGameOver(
                        errorIndex = viewModel.playerSeq.size,
                        isActualError = false
                    )
                }
                onRecap()
            },
            enabled = viewModel.state != GameState.READY,
            modifier = btnModifier,
            shape = btnShape
        ) {
            Text(stringResource(R.string.end), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}