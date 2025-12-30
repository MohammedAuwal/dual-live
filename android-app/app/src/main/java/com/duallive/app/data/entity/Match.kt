package com.duallive.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "matches",
    indices = [Index(value = ["leagueId"])],
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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val leagueId: Long,
    val homeTeamId: Long,
    val awayTeamId: Long,
    val homeScore: Int,
    val awayScore: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val stage: String = "",  // For UCL: "GROUP", "QF", "SF", "FINAL"
    val isKnockout: Boolean = false
)
