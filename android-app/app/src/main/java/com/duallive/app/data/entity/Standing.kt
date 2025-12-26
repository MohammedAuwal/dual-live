package com.duallive.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "standings")
data class Standing(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val teamId: Int,
    val matchesPlayed: Int = 0,
    val wins: Int = 0,
    val draws: Int = 0,
    val losses: Int = 0,
    val points: Int = 0
)
