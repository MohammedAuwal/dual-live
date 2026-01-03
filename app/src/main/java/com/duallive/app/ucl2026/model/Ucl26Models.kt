package com.duallive.app.ucl2026.model

// The Team in the Swiss Table
data class Ucl26Team(
    val teamId: Int,
    val teamName: String,
    var matchesPlayed: Int = 0,
    var points: Int = 0,
    var goalsFor: Int = 0,
    var goalsAgainst: Int = 0,
    var goalDifference: Int = 0
)

// The Match in the Swiss League Stage
data class Ucl26Match(
    val matchId: Int,
    val homeTeamId: Int,
    val awayTeamId: Int,
    var homeScore: Int = 0,
    var awayScore: Int = 0,
    var isPlayed: Boolean = false
)

// The Match in the Knockout Bracket (Play-offs & R16)
data class BracketMatch(
    val id: Int,
    val team1Name: String,
    val team2Name: String,
    var aggregate1: Int = 0,
    var aggregate2: Int = 0,
    var isFinished: Boolean = false
)
