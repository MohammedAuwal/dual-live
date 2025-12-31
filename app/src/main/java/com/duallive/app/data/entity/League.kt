package com.duallive.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leagues")
data class League(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String? = null,
    val isHomeAndAway: Boolean = false,
    val type: LeagueType = LeagueType.CLASSIC,
    val inviteCode: String = ""
)
