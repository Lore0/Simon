package com.example.simon.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val maxCorrectLength: Int,
    val sequence: String,
    val errorIndex: Int
)