package com.duallive.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class Team(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val leagueId: Int, // Link to the League
    val name: String,
    val logoPath: String? = null,
    val groupName: String? = null // For UCL groups
)
