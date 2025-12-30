package com.duallive.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "matches",
    foreignKeys = [
        ForeignKey(
            entity = League::class,
            parentColumns = ["id"],
            childColumns = ["leagueId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Match(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val leagueId: Int,
    val homeTeamId: Int,
    val awayTeamId: Int,
    val homeScore: Int,
    val awayScore: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val stage: String = "",  // For UCL: "GROUP", "RO16", "QF", "SF", "FINAL"
    val isKnockout: Boolean = false  // For Classic League knockout matches
)
