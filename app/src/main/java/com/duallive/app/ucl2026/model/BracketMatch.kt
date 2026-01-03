package com.duallive.app.ucl2026.model

data class BracketMatch(
    val id: Int,
    val roundName: String,
    val team1Name: String,
    val team2Name: String,
    val leg1Score1: Int = 0,
    val leg1Score2: Int = 0,
    val leg2Score1: Int = 0,
    val leg2Score2: Int = 0,
    val isCompleted: Boolean = false
) {
    val aggregate1: Int get() = leg1Score1 + leg2Score1
    val aggregate2: Int get() = leg1Score2 + leg2Score2
}
