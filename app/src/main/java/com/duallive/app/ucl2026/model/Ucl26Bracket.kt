package com.duallive.app.ucl2026.model

data class BracketMatch(
    val id: Int,
    val roundName: String, // QF, SF, Final
    val team1Name: String,
    val team2Name: String,
    val leg1Score1: Int? = null,
    val leg1Score2: Int? = null,
    val leg2Score1: Int? = null,
    val leg2Score2: Int? = null,
    val isCompleted: Boolean = false
) {
    val aggregate1: Int get() = (leg1Score1 ?: 0) + (leg2Score1 ?: 0)
    val aggregate2: Int get() = (leg1Score2 ?: 0) + (leg2Score2 ?: 0)
}
