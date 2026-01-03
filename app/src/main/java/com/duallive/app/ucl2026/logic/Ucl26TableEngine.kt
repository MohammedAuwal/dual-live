package com.duallive.app.ucl2026.logic

import com.duallive.app.ucl2026.model.Ucl26StandingRow

class Ucl26TableEngine {
    fun rankTeams(teams: List<Ucl26StandingRow>): List<Ucl26StandingRow> {
        return teams.sortedWith(
            compareByDescending<Ucl26StandingRow> { it.points }
                .thenByDescending { it.goalDifference }
                .thenByDescending { it.goalsFor }
        )
    }

    fun getQualificationZone(position: Int): UclZone {
        return when (position) {
            in 1..8 -> UclZone.DIRECT_R16
            in 9..24 -> UclZone.PLAYOFFS
            else -> UclZone.ELIMINATED
        }
    }
}

enum class UclZone {
    DIRECT_R16, PLAYOFFS, ELIMINATED
}
