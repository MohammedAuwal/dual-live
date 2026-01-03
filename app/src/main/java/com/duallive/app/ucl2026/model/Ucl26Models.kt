package com.duallive.app.ucl2026.model

data class Ucl26Team(
    val teamId: Int,
    val teamName: String,
    val matchesPlayed: Int = 0,
    val goalsScored: Int = 0,
    val goalsConceded: Int = 0,
    val points: Int = 0
) {
    val goalDifference: Int get() = goalsScored - goalsConceded
}

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
    val roundName: String = "R16",
    val team1Name: String,
    val team2Name: String,
    var leg1Score1: Int = 0,
    var leg1Score2: Int = 0,
    var leg2Score1: Int = 0,
    var leg2Score2: Int = 0,
    var isFinished: Boolean = false
) {
    val aggregate1: Int get() = leg1Score1 + (if (roundName != "Final") leg2Score1 else 0)
    val aggregate2: Int get() = leg1Score2 + (if (roundName != "Final") leg2Score2 else 0)
}
