package com.duallive.app.data.entity

data class Standing(
    val teamId: Int,
    var matchesPlayed: Int = 0,
    var wins: Int = 0,
    var draws: Int = 0,
    var losses: Int = 0,
    var goalsFor: Int = 0,     // ADDED
    var goalsAgainst: Int = 0, // ADDED
    var points: Int = 0
)
