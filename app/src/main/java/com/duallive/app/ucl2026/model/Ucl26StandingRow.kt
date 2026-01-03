package com.duallive.app.ucl2026.model

data class Ucl26StandingRow(
    val teamId: Int,
    val teamName: String,
    val matchesPlayed: Int = 0,
    val points: Int = 0,
    val goalDifference: Int = 0
)
