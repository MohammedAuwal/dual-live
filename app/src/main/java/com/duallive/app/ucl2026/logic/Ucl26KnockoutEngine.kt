package com.duallive.app.ucl2026.logic

import com.duallive.app.ucl2026.model.Ucl26StandingRow

data class Ucl26PlayoffTie(
    val seededTeamId: Int,
    val unseededTeamId: Int,
    val homeScoreLeg1: Int = 0,
    val awayScoreLeg1: Int = 0,
    val homeScoreLeg2: Int = 0,
    val awayScoreLeg2: Int = 0,
    var winnerId: Int? = null
)

class Ucl26KnockoutEngine {

    /**
     * Pairs teams 9-16 (seeded) with teams 17-24 (unseeded)
     */
    fun generatePlayoffTies(rankedStandings: List<Ucl26StandingRow>): List<Ucl26PlayoffTie> {
        val seeded = rankedStandings.subList(8, 16).shuffled()
        val unseeded = rankedStandings.subList(16, 24).shuffled()
        
        return seeded.mapIndexed { index, seededTeam ->
            Ucl26PlayoffTie(
                seededTeamId = seededTeam.teamId,
                unseededTeamId = unseeded[index].teamId
            )
        }
    }

    /**
     * Calculates the winner of a two-legged tie
     * Note: Away goals rule is NOT used as per current UEFA rules
     */
    fun resolveWinner(tie: Ucl26PlayoffTie, aggregatePenaltyScore: Pair<Int, Int>? = null): Int {
        val totalSeeded = tie.awayScoreLeg1 + tie.homeScoreLeg2
        val totalUnseeded = tie.homeScoreLeg1 + tie.awayScoreLeg2
        
        return when {
            totalSeeded > totalUnseeded -> tie.seededTeamId
            totalUnseeded > totalSeeded -> tie.unseededTeamId
            else -> aggregatePenaltyScore?.let { 
                if (it.first > it.second) tie.seededTeamId else tie.unseededTeamId 
            } ?: -1 // -1 signifies it needs penalties
        }
    }
}
