package com.example.simon

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simon.db.Game
import com.example.simon.db.GameDatabase
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application) {
    private val gameDao = GameDatabase.getDatabase(application).gameDao()
    var gamesList = mutableStateListOf<Game>()
    init {
        loadGames()
    }
    fun loadGames() {
        viewModelScope.launch {
            val savedGames = gameDao.getAllGames()
            gamesList.clear()
            gamesList.addAll(savedGames)
        }
    }
}