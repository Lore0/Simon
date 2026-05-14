package com.example.simon

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simon.db.Game
import com.example.simon.db.GameDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

enum class GameState {
    READY,          // Partita non ancora iniziata
    COMP_TURN,      // Il computer sta mostrando la sequenza di colori
    PLAYER_TURN,    // Il computer ha finito, il giocatore deve premere i tasti
    PAUSED,         // Il gioco è in pausa (possibile solo durante COMP_TURN)
    GAME_OVER       // Il giocatore ha sbagliato o ha premuto "Fine partita"
    // todo : gestire differenza tra sbagliato e "fine partita"
}

class GameViewModel (application: Application) : AndroidViewModel(application) {
    // init db
    private val gameDao = GameDatabase.getDatabase(application).gameDao()
    var state by mutableStateOf(GameState.READY)
    var activeColor by mutableStateOf<String?>(null)

    val compSeq = mutableStateListOf<String>()
    val playerSeq = mutableStateListOf<String>()
    val colors = listOf("R", "G", "B", "Y", "C", "M")

    fun startGame() {
        if (state != GameState.READY) return
        compSeq.clear()
        playerSeq.clear()
        addNewColor()
        playCompSeq()
    }

    private fun addNewColor() {
        compSeq.add(colors.random())
    }

    private fun playCompSeq() {
        viewModelScope.launch {
            state = GameState.COMP_TURN
            playerSeq.clear()
            delay(500)

            for (color in compSeq) {
                while (state == GameState.PAUSED) {
                    delay(100)
                }

                if (state == GameState.GAME_OVER) return@launch

                activeColor = color
                delay(600)
                activeColor = null
                delay(300)
            }

            state = GameState.PLAYER_TURN
        }
    }
}