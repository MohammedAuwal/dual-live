package com.duallive.app.ucl2026.logic

import com.duallive.app.ucl2026.model.Ucl26StandingRow

data class Ucl26BracketTie(
    val roundName: String, // "R16", "QF", "SF", "F"
    val teamAId: Int?,
    val teamBId: Int?,
    val isFinished: Boolean = false,
    val winnerId: Int? = null
)

class Ucl26BracketEngine {
    /**
     * Predetermined Pairings for R16 based on League Position:
     * Pairing A: 1/2 vs Winner of Play-off (15/16/17/18)
     * Pairing B: 3/4 vs Winner of Play-off (13/14/19/20)
     * ... and so on.
     */
    fun createR16Bracket(top8: List<Ucl26StandingRow>, playoffWinners: List<Int>): List<Ucl26BracketTie> {
        // In a real implementation, we follow the specific UEFA bracket paths (Pairing A-D)
        return List(8) { i ->
            Ucl26BracketTie(
                roundName = "Round of 16",
                teamAId = top8.getOrNull(i)?.teamId,
                teamBId = playoffWinners.getOrNull(i)
            )
        }
    }
}
