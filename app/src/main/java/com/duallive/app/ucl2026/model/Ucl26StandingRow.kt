package com.duallive.app.ucl2026.model

data class Ucl26StandingRow(
    val teamId: Int,
    val teamName: String,
    val matchesPlayed: Int = 0,
    val wins: Int = 0,
    val draws: Int = 0,
    val losses: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val points: Int = 0
) {
    val goalDifference: Int get() = goalsFor - goalsAgainst
}
