package com.duallive.app.ucl2026.model

data class Ucl26Match(
    val matchId: Int,
    val homeTeamId: Int,
    val awayTeamId: Int,
    var homeScore: Int? = null,
    var awayScore: Int? = null,
    var isPlayed: Boolean = false
)
