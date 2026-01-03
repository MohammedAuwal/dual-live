package com.duallive.app.ucl2026.model

data class Ucl26Team(
    val teamId: Int,
    val teamName: String,
    var matchesPlayed: Int = 0,
    var points: Int = 0,
    var goalsFor: Int = 0,
    var goalsAgainst: Int = 0,
    var goalDifference: Int = 0
)

data class Ucl26Match(
    val matchId: Int,
    val homeTeamId: Int,
    val awayTeamId: Int,
    var homeScore: Int = 0,
    var awayScore: Int = 0,
    var isPlayed: Boolean = false
)

data class BracketMatch(
    val id: Int,
    val team1Name: String,
    val team2Name: String,
    var aggregate1: Int = 0,
    var aggregate2: Int = 0,
    var isFinished: Boolean = false
)
