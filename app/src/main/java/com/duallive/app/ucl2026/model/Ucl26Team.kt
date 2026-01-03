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
