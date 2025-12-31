package com.duallive.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class Match(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val leagueId: Int,
    val homeTeamId: Int,
    val awayTeamId: Int,
    val homeScore: Int,
    val awayScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)
