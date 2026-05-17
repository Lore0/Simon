package com.example.simon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simon.ui.theme.SimonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimonTheme {
                val navController = rememberNavController()
                val listViewModel: ListViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController, startDestination = "list",
                        modifier = Modifier.padding(innerPadding),
                        // diminuita animazione di default (troppo lenta)
                        enterTransition = { fadeIn(tween(150)) },
                        exitTransition = { fadeOut(tween(150)) },
                    ) {
                        composable("game") {
                            val gameViewModel: GameViewModel = viewModel()
                            GameScreen(
                                viewModel = gameViewModel,
                                onRecap = {
                                    // torno nella prima schermata (lista)
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("list") {
                            listViewModel.loadGames()
                            ListScreen(
                                viewModel = listViewModel,
                                onPlayGame = { navController.navigate("game") },
                                onDetail = { clickedGame ->
                                    listViewModel.selectedGame = clickedGame
                                    navController.navigate("detail")
                                }
                            )
                        }
                        composable("detail") {
                            val game = listViewModel.selectedGame
                            if (game != null) DetailScreen(game = game)
                            else navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}