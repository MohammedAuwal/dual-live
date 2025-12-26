package com.duallive.app.utils

import com.duallive.app.data.entity.Team

data class Fixture(val homeTeam: Team, val awayTeam: Team)

object FixtureGenerator {
    fun generateRoundRobin(teams: List<Team>, homeAndAway: Boolean): List<Fixture> {
        if (teams.size < 2) return emptyList()
        
        val fixtures = mutableListOf<Fixture>()
        val teamList = teams.toMutableList()
        
        // Use the circle algorithm for the first leg
        val totalRounds = if (teamList.size % 2 == 0) teamList.size - 1 else teamList.size
        val matchesPerRound = teamList.size / 2

        for (round in 0 until totalRounds) {
            for (match in 0 until matchesPerRound) {
                val homeIdx = (round + match) % (teamList.size - 1)
                var awayIdx = (teamList.size - 1 - match + round) % (teamList.size - 1)
                if (match == 0) awayIdx = teamList.size - 1

                fixtures.add(Fixture(teamList[homeIdx], teamList[awayIdx]))
            }
        }

        // If Home & Away is selected, add the return leg
        if (homeAndAway) {
            val returnLeg = fixtures.map { Fixture(it.awayTeam, it.homeTeam) }
            fixtures.addAll(returnLeg)
        }

        return fixtures.shuffled()
    }
}
