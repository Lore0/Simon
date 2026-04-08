package com.example.simon

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Screen1(onRecap: () -> Unit) {
    val buttons = listOf(
        Color.Red to "R",
        Color.Green to "G",
        Color.Blue to "B",
        Color.Yellow to "Y",
        Color.Cyan to "C",
        Color.Magenta to "M",
    )

    val orientation = LocalConfiguration.current.orientation

    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        // orizzontale
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp), // Padding ridotto per massimizzare lo spazio
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(buttons) { (colore, lettera) ->
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = colore),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .height(95.dp),
                        shape = RectangleShape
                    ) {}
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "C, Y, R...",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    ) {
                        Text(stringResource(R.string.clear), color = Color.Black)
                    }
                    Button(
                        onClick = onRecap,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.end))
                    }
                }
            }
        }
    } else {
        // verticale
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(buttons) { (colore, lettera) ->
                    Button(
                        onClick = {
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colore),
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RectangleShape
                    ) {
                    }
                }
            }
            Text(
                text = "C, Y, R, M, ...",
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text(
                        text = stringResource(R.string.clear),
                        color = Color.Black)
                }

                Button(
                    onClick = onRecap,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.end),
                    )
                }
            }
        }
    }
}