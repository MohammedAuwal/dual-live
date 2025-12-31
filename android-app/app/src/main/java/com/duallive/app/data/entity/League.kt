package com.duallive.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LeagueType {
    CLASSIC, UCL
}

@Entity(tableName = "leagues")
data class League(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val isHomeAndAway: Boolean = false,
    val type: LeagueType,
    val inviteCode: String = "DL-${(1000..9999).random()}" = LeagueType.CLASSIC
)
