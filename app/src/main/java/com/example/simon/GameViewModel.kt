package com.example.simon

import android.app.Application
import android.media.SoundPool
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
    READY,       // partita non ancora iniziata
    COMP_TURN,   // il computer mostra la sequenza
    PLAYER_TURN, // tocca al giocatore
    PAUSED,      // pausa (solo durante COMP_TURN)
    GAME_OVER    // errore o "Fine partita"
}

class GameViewModel (application: Application) : AndroidViewModel(application) {
    // sound
    private val soundPool: SoundPool = SoundPool.Builder().build()
    private val soundMap = mutableMapOf<String, Int>()

    init{
        soundMap["R"] = soundPool.load(application, R.raw.r, 1)
        soundMap["G"] = soundPool.load(application, R.raw.g, 1)
        soundMap["B"] = soundPool.load(application, R.raw.b, 1)
        soundMap["M"] = soundPool.load(application, R.raw.m, 1)
        soundMap["Y"] = soundPool.load(application, R.raw.y, 1)
        soundMap["C"] = soundPool.load(application, R.raw.c, 1)
    }

    // init db
    private val gameDao = GameDatabase.getDatabase(application).gameDao()
    var state by mutableStateOf(GameState.READY)
    var activeColor by mutableStateOf<String?>(null)
    var actualError by mutableStateOf(false)

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

    private fun playSound(color: String){
        val soundID = soundMap[color] ?: return
        soundPool.play(soundID, 1f, 1f, 1, 0, 1f)
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
                while (state == GameState.PAUSED) delay(100)
                if (state == GameState.GAME_OVER) return@launch

                activeColor = color
                playSound(color)
                delay(600)
                activeColor = null
                delay(300)
            }

            state = GameState.PLAYER_TURN
        }
    }

    fun onPlayerTurn(color: String) {
        if (state != GameState.PLAYER_TURN) return

        viewModelScope.launch {
            activeColor = color
            delay(150)
            activeColor = null
        }

        playSound(color)
        playerSeq.add(color)

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
        } else if (state == GameState.PAUSED) {
            state = GameState.COMP_TURN
        }
    }

    // default se premo direttamente fine partita (size)
    fun handleGameOver(errorIndex: Int = playerSeq.size, isActualError: Boolean = false) {
        if (state == GameState.GAME_OVER) return
        state = GameState.GAME_OVER
        actualError = isActualError

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