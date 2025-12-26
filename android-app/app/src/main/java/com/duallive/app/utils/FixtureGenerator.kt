package com.duallive.app.utils

import com.duallive.app.data.entity.Team

data class Fixture(val homeTeam: Team, val awayTeam: Team)

object FixtureGenerator {
    fun generateRoundRobin(teams: List<Team>): List<Fixture> {
        if (teams.size < 2) return emptyList()
        
        val teamList = teams.toMutableList()
        if (teamList.size % 2 != 0) {
            // We don't add a 'Bye', we just handle odd numbers by skipping the odd one out each round
        }

        val fixtures = mutableListOf<Fixture>()
        val totalRounds = if (teamList.size % 2 == 0) teamList.size - 1 else teamList.size
        val matchesPerRound = teamList.size / 2

        for (round in 0 until totalRounds) {
            for (match in 0 until matchesPerRound) {
                val home = (round + match) % (teamList.size - 1)
                var away = (teamList.size - 1 - match + round) % (teamList.size - 1)

                if (match == 0) away = teamList.size - 1

                fixtures.add(Fixture(teamList[home], teamList[away]))
            }
        }
        return fixtures.shuffled() // Shuffle so the order isn't predictable
    }
}
