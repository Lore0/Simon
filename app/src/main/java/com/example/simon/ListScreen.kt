package com.example.simon

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simon.db.Game

@Composable
fun ListScreen(
    viewModel: ListViewModel,
    onPlayGame: () -> Unit,
    onDetail: (Game) -> Unit
) {
    // prendo da listViewModel
    val games = viewModel.gamesList

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onPlayGame) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.new_game)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp, bottom = innerPadding.calculateBottomPadding())
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.recap),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (games.isEmpty()) {
                Text(
                    text = stringResource(R.string.empty_games),
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 32.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(games) { game ->
                        GamesRecap(game, onClick = { onDetail(game) } )
                    }
                }
            }
        }
    }
}

@Composable
fun GamesRecap(game: Game, onClick: () -> Unit) {
    val items = game.sequence.split(",")
    // Dividiamo la lista in due (prima e dopo l'indice dove ha sbagliato)
    val correct = items.take(game.errorIndex).joinToString(", ")
    val wrong = items.drop(game.errorIndex).joinToString(", ")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{ onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = game.maxCorrectLength.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(0.2f)
            )
            Text(
                text = buildAnnotatedString {
                    append(correct)
                    if (correct.isNotEmpty() && wrong.isNotEmpty()) append(", ")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append(wrong)
                    }
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 18.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.8f)
            )
        }
    }
}