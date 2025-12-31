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

            // If groupName is null, it means we are in UCL Knockouts. 
            // We force knockout draw to prevent the 15-match league bug.
            if (groupName == null) {
                allFixtures.addAll(generateKnockoutDraw(groupTeams, "Knockout"))
                return@forEach
            }

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
            allFixtures.addAll(groupFixtures)
        }
        return allFixtures
    }

    fun generateKnockoutDraw(teams: List<Team>, stageLabel: String): List<Fixture> {
        val shuffled = teams.shuffled()
        val fixtures = mutableListOf<Fixture>()
        // Creates exactly 1 match for every 2 teams (e.g., 6 teams = 3 matches)
        for (i in 0 until (shuffled.size / 2) * 2 step 2) {
            fixtures.add(Fixture(1, shuffled[i], shuffled[i+1], stageLabel))
        }
        return fixtures
    }
}
