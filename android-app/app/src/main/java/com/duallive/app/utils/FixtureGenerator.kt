package com.duallive.app.utils

import com.duallive.app.data.entity.Team

data class Fixture(val round: Int, val homeTeam: Team, val awayTeam: Team)

object FixtureGenerator {
    fun generateRoundRobin(teams: List<Team>, homeAndAway: Boolean): List<Fixture> {
        if (teams.size < 2) return emptyList()
        
        // Split teams by group. If Classic, everyone is in one "null" group.
        val groupedTeams = teams.groupBy { it.groupName }
        val allFixtures = mutableListOf<Fixture>()

        groupedTeams.forEach { (_, groupTeams) ->
            if (groupTeams.size < 2) return@forEach

            // Your original Classic logic starts here
            val teamList = if (groupTeams.size % 2 != 0) {
                groupTeams + Team(id = -1, leagueId = -1, name = "BYE")
            } else {
                groupTeams
            }
            
            val numTeams = teamList.size
            val numRounds = numTeams - 1
            val matchesPerRound = numTeams / 2
            val groupFixtures = mutableListOf<Fixture>()

            for (round in 0 until numRounds) {
                for (match in 0 until matchesPerRound) {
                    val home = (round + match) % (numTeams - 1)
                    var away = (numTeams - 1 - match + round) % (numTeams - 1)
                    if (match == 0) away = numTeams - 1

                    val homeTeam = teamList[home]
                    val awayTeam = teamList[away]

                    if (homeTeam.id != -1 && awayTeam.id != -1) {
                        groupFixtures.add(Fixture(round + 1, homeTeam, awayTeam))
                    }
                }
            }

            if (homeAndAway) {
                val secondLeg = groupFixtures.map { 
                    it.copy(round = it.round + numRounds, homeTeam = it.awayTeam, awayTeam = it.homeTeam) 
                }
                groupFixtures.addAll(secondLeg)
            }
            // Your original Classic logic ends here
            
            allFixtures.addAll(groupFixtures)
        }

        return allFixtures
    }
}
