package com.duallive.app.utils

import com.duallive.app.data.entity.Team

data class Fixture(val round: Int, val homeTeam: Team, val awayTeam: Team, val label: String = "")

object FixtureGenerator {
    fun generateRoundRobin(teams: List<Team>, homeAndAway: Boolean): List<Fixture> {
        if (teams.size < 2) return emptyList()
        
        val groupedTeams = teams.groupBy { it.groupName }
        val allFixtures = mutableListOf<Fixture>()

        groupedTeams.forEach { (groupName, groupTeams) ->
            if (groupTeams.size < 2) return@forEach

            // UCL Fix: If teams have no group name, they are in a Knockout stage.
            // We don't want a league; we want one-off matches.
            if (groupName == null && groupTeams.size <= 8) {
                allFixtures.addAll(generateKnockoutDraw(groupTeams, "Knockout"))
                return@forEach
            }

            val teamList = groupTeams.toMutableList()
            // Add a dummy team if odd number, but we won't add it to the final list
            val hasBye = teamList.size % 2 != 0
            if (hasBye) {
                teamList.add(Team(id = -1, leagueId = -1, name = "BYE"))
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

                    // Only add the match if neither team is the "BYE" team
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
            allFixtures.addAll(groupFixtures)
        }
        return allFixtures
    }

    fun generateKnockoutDraw(teams: List<Team>, stageLabel: String): List<Fixture> {
        val shuffled = teams.shuffled()
        val fixtures = mutableListOf<Fixture>()
        // Creates exactly 1 match for every 2 teams (e.g., 8 teams = 4 matches)
        for (i in 0 until (shuffled.size / 2) * 2 step 2) {
            fixtures.add(Fixture(1, shuffled[i], shuffled[i+1], stageLabel))
        }
        return fixtures
    }
}
