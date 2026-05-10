package com.example.simon.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY id DESC")
    suspend fun getAllGames(): List<Game>

    @Insert
    suspend fun insertGame(game: Game)
}