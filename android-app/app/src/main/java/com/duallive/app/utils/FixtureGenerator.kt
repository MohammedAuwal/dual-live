package com.duallive.app.utils

import com.duallive.app.data.entity.Team

data class Fixture(val round: Int, val homeTeam: Team, val awayTeam: Team, val label: String = "")

object FixtureGenerator {
    
    // SAFE ROUND ROBIN: Only runs for actual leagues or group stages
    fun generateRoundRobin(teams: List<Team>, homeAndAway: Boolean): List<Fixture> {
        if (teams.size < 2) return emptyList()
        
        // Safety check: If these teams have no group names, they shouldn't be playing a 15-match league
        val groupedTeams = teams.groupBy { it.groupName }
        val allFixtures = mutableListOf<Fixture>()

        groupedTeams.forEach { (groupName, groupTeams) ->
            if (groupTeams.size < 2) return@forEach

            // If it's a knockout stage, we don't use this math
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

    // UPDATED: Specifically for UCL Knockout Stages (Safe & Accurate)
    fun generateKnockoutDraw(teams: List<Team>, stageLabel: String): List<Fixture> {
        // Only take even pairs. If 6 teams, make 3 matches. If 4 teams, 2 matches.
        val shuffled = teams.shuffled()
        val fixtures = mutableListOf<Fixture>()
        
        // We use round 1 here so it doesn't show "Round 99"
        val displayRound = when {
            stageLabel.contains("Quarter") -> 1
            stageLabel.contains("Semi") -> 2
            stageLabel.contains("Final") -> 3
            else -> 1
        }
        
        for (i in 0 until (shuffled.size / 2) * 2 step 2) {
            fixtures.add(
                Fixture(
                    round = displayRound, 
                    homeTeam = shuffled[i],
                    awayTeam = shuffled[i+1],
                    label = stageLabel
                )
            )
        }
        return fixtures
    }
}
