package com.example.simon.viewmodel

import android.app.Application
import android.media.SoundPool
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.simon.R
import com.example.simon.db.Game
import com.example.simon.db.GameDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

enum class GameState {
    READY,       // partita non ancora iniziata
    COMP_TURN,   // il computer mostra la sequenza
    PLAYER_TURN, // turno del giocatore
    PAUSED,      // pausa (solo durante COMP_TURN)
    GAME_OVER    // errore o "Fine partita"
}

class GameViewModel (
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    // init db
    private val gameDao = GameDatabase.getDatabase(application).gameDao()

    // sound
    private val soundPool: SoundPool = SoundPool.Builder().build()
    private val soundMap = mutableMapOf<String, Int>()

    var state by mutableStateOf(savedStateHandle["state"] ?: GameState.READY)
    var activeColor by mutableStateOf<String?>(savedStateHandle["color"])
    var actualError by mutableStateOf(savedStateHandle["error"] ?: false)
    var flashAll by mutableStateOf(false)

    val compSeq = (savedStateHandle["comp"] ?: emptyList<String>()).toMutableStateList()
    val playerSeq = (savedStateHandle["player"] ?: emptyList<String>()).toMutableStateList()
    val colors = listOf("R", "G", "B", "Y", "C", "M")

    init{
        soundMap["R"] = soundPool.load(application, R.raw.r, 1)
        soundMap["G"] = soundPool.load(application, R.raw.g, 1)
        soundMap["B"] = soundPool.load(application, R.raw.b, 1)
        soundMap["M"] = soundPool.load(application, R.raw.m, 1)
        soundMap["Y"] = soundPool.load(application, R.raw.y, 1)
        soundMap["C"] = soundPool.load(application, R.raw.c, 1)
        soundMap["game_over"] = soundPool.load(application, R.raw.game_over, 1)

        // TODO: da scrivere specifiche README -> decisione quando ripristino stato
        if (state == GameState.COMP_TURN) {
            activeColor = null
            savedStateHandle["color"] = null
            // rifacciamo vedere seq da inzio
            playCompSeq()
        } else if (state == GameState.PLAYER_TURN) {
            // utente riprende da dove si era fermato
            activeColor = null
            savedStateHandle["color"] = null
        }
    }

    fun startGame() {
        if (state != GameState.READY) return
        compSeq.clear()
        addNewColor()
        playCompSeq()
    }

    private fun playSound(color: String){
        val soundID = soundMap[color] ?: return
        soundPool.play(soundID, 1f, 1f, 1, 0, 1f)
    }

    private fun playGameOverSound() {
        val soundId = soundMap["game_over"] ?: return
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    private fun addNewColor() {
        compSeq.add(colors.random())
        savedStateHandle["comp"] = ArrayList(compSeq)
    }

    private fun playCompSeq() {
        viewModelScope.launch {
            state = GameState.COMP_TURN
            savedStateHandle["state"] = state
            playerSeq.clear()
            savedStateHandle["player"] = ArrayList(playerSeq)
            delay(500)

            for (color in compSeq) {
                while (state == GameState.PAUSED) delay(100)
                if (state == GameState.GAME_OVER) return@launch

                activeColor = color
                savedStateHandle["color"] = color
                playSound(color)
                delay(600)
                activeColor = null
                savedStateHandle["color"] = null
                delay(300)
            }

            state = GameState.PLAYER_TURN
            savedStateHandle["state"] = state
        }
    }

    fun onPlayerTurn(color: String) {
        if (state != GameState.PLAYER_TURN) return

        viewModelScope.launch {
            activeColor = color
            savedStateHandle["color"] = color
            delay(150)
            activeColor = null
            savedStateHandle["color"] = null
        }

        playSound(color)
        playerSeq.add(color)
        savedStateHandle["player"] = ArrayList(playerSeq)

        val currentIndex = playerSeq.size - 1
        if (playerSeq[currentIndex] == compSeq[currentIndex]) {
            // e se siamo alla fine della seq -> vai alla next seq
            if (playerSeq.size == compSeq.size) {
                viewModelScope.launch {
                    // delay per far vedere l'ultima lettera
                    delay(500)
                    addNewColor()
                    playCompSeq()
                }
            }
        } else {
            handleGameOver(currentIndex, true)
        }
    }

    fun onPause() {
        if (state == GameState.COMP_TURN) {
            state = GameState.PAUSED
            savedStateHandle["state"] = state
        } else if (state == GameState.PAUSED) {
            state = GameState.COMP_TURN
            savedStateHandle["state"] = state
        }
    }

    // default se premo direttamente fine partita (size)
    fun handleGameOver(errorIndex: Int = playerSeq.size, isActualError: Boolean = false) {
        if (state == GameState.GAME_OVER) return
        state = GameState.GAME_OVER
        savedStateHandle["state"] = state

        if (isActualError) {
            viewModelScope.launch {
                playGameOverSound()
                flashAll = true
                delay(800)
                flashAll = false
                actualError = true
                savedStateHandle["error"] = true
            }
        } else {
            actualError = false
            savedStateHandle["error"] = false
        }

        // non salviamo seq da 1
        if (compSeq.size <= 1) return

        val newGame = Game(
            maxCorrectLength = compSeq.size - 1,
            sequence = compSeq.joinToString(","),
            errorIndex = errorIndex
        )
        viewModelScope.launch {
            gameDao.insertGame(newGame)
        }
    }
}