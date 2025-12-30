package com.duallive.app.utils

import com.duallive.app.data.entity.Team
import com.duallive.app.data.entity.Match

data class Fixture(
    val round: Int, 
    val homeTeam: Team, 
    val awayTeam: Team, 
    val label: String = "",
    val stage: String = "",
    val matchNumber: Int = 0,
    val isKnockout: Boolean = false
)

object FixtureGenerator {
    
    fun generateRoundRobin(teams: List<Team>, homeAndAway: Boolean): List<Fixture> {
        if (teams.size < 2) return emptyList()
        val teamList = teams.toMutableList()
        if (teamList.size % 2 != 0) {
            teamList.add(Team(id = -1L, leagueId = -1L, name = "BYE"))
        }
        
        val numTeams = teamList.size
        val numRounds = numTeams - 1
        val matchesPerRound = numTeams / 2
        val fixtures = mutableListOf<Fixture>()

        for (round in 0 until numRounds) {
            for (match in 0 until matchesPerRound) {
                val home = (round + match) % (numTeams - 1)
                var away = (numTeams - 1 - match + round) % (numTeams - 1)
                if (match == 0) away = numTeams - 1

                val homeTeam = teamList[home]
                val awayTeam = teamList[away]

                if (homeTeam.id != -1L && awayTeam.id != -1L) {
                    fixtures.add(Fixture(round + 1, homeTeam, awayTeam))
                }
            }
        }

        if (homeAndAway) {
            val secondLeg = fixtures.map { 
                it.copy(round = it.round + numRounds, homeTeam = it.awayTeam, awayTeam = it.homeTeam) 
            }
            fixtures.addAll(secondLeg)
        }
        return fixtures
    }
    
    fun generateUCLGroupFixtures(groupTeams: List<Team>, homeAndAway: Boolean): List<Fixture> {
        if (groupTeams.size < 2) return emptyList()
        val teamList = groupTeams.toMutableList()
        if (teamList.size % 2 != 0) {
            teamList.add(Team(id = -1L, leagueId = -1L, name = "BYE"))
        }
        
        val numTeams = teamList.size
        val numRounds = numTeams - 1
        val matchesPerRound = numTeams / 2
        val fixtures = mutableListOf<Fixture>()

        for (round in 0 until numRounds) {
            for (match in 0 until matchesPerRound) {
                val home = (round + match) % (numTeams - 1)
                var away = (numTeams - 1 - match + round) % (numTeams - 1)
                if (match == 0) away = numTeams - 1

                val homeTeam = teamList[home]
                val awayTeam = teamList[away]

                if (homeTeam.id != -1L && awayTeam.id != -1L) {
                    fixtures.add(Fixture(round + 1, homeTeam, awayTeam, stage = "GROUP"))
                }
            }
        }

        if (homeAndAway) {
            val secondLeg = fixtures.map { 
                it.copy(round = it.round + numRounds, homeTeam = it.awayTeam, awayTeam = it.homeTeam) 
            }
            fixtures.addAll(secondLeg)
        }
        return fixtures
    }

    fun generateKnockoutDraw(teams: List<Team>, stageLabel: String): List<Fixture> {
        val shuffled = teams.shuffled()
        val fixtures = mutableListOf<Fixture>()
        for (i in 0 until (shuffled.size / 2) * 2 step 2) {
            fixtures.add(Fixture(
                round = 1,
                homeTeam = shuffled[i],
                awayTeam = shuffled[i+1],
                label = stageLabel,
                stage = stageLabel,
                matchNumber = i/2 + 1,
                isKnockout = true
            ))
        }
        return fixtures
    }

    fun generateUCLRoundOf16Draw(groups: Map<String?, List<Team>>): List<Fixture> {
        val fixtures = mutableListOf<Fixture>()
        val groupWinners = mutableListOf<Team>()
        val groupRunnersUp = mutableListOf<Team>()
        
        for (group in 'A'..'H') {
            val groupKey = group.toString()
            val teamsInGroup = groups[groupKey] ?: continue
            if (teamsInGroup.size >= 2) {
                groupWinners.add(teamsInGroup[0])
                groupRunnersUp.add(teamsInGroup[1])
            }
        }
        
        val shuffledRunnersUp = groupRunnersUp.shuffled().toMutableList()
        for ((index, winner) in groupWinners.withIndex()) {
            var runnerUp: Team? = null
            var runnerUpIndex = -1
            for (i in shuffledRunnersUp.indices) {
                if (shuffledRunnersUp[i].groupName != winner.groupName) {
                    runnerUp = shuffledRunnersUp[i]
                    runnerUpIndex = i
                    break
                }
            }
            if (runnerUp != null) {
                fixtures.add(Fixture(1, winner, runnerUp, "Round of 16", "RO16", index + 1, true))
                shuffledRunnersUp.removeAt(runnerUpIndex)
            }
        }
        return fixtures
    }

    fun generateNextKnockoutRound(previousRoundWinners: List<Team>, stage: String): List<Fixture> {
        val fixtures = mutableListOf<Fixture>()
        val shuffled = previousRoundWinners.shuffled()
        for (i in 0 until (shuffled.size / 2) * 2 step 2) {
            fixtures.add(Fixture(1, shuffled[i], shuffled[i+1], stage, stage, i/2 + 1, true))
        }
        return fixtures
    }

    fun getWinnersFromKnockoutRound(matches: List<Match>, teams: List<Team>, stage: String): List<Team> {
        val winners = mutableListOf<Team>()
        val teamMap = teams.associateBy { it.id }
        for (match in matches.filter { it.stage == stage }) {
            val homeTeam = teamMap[match.homeTeamId]
            val awayTeam = teamMap[match.awayTeamId]
            if (homeTeam != null && awayTeam != null) {
                if (match.homeScore >= match.awayScore) winners.add(homeTeam) else winners.add(awayTeam)
            }
        }
        return winners
    }

    fun isUCLGroupStageComplete(matches: List<Match>): Boolean = matches.filter { it.stage == "GROUP" }.size >= 192

    fun getTopTeamsForUCLKnockout(teams: List<Team>, standings: Map<Team, Int>): Pair<List<Team>, List<Team>> {
        val winners = mutableListOf<Team>(); val runnersUp = mutableListOf<Team>()
        teams.groupBy { it.groupName }.forEach { (name, gTeams) ->
            if (name != null && name in "ABCDEFGH") {
                val sorted = gTeams.sortedByDescending { standings[it] ?: 0 }
                if (sorted.isNotEmpty()) winners.add(sorted[0])
                if (sorted.size >= 2) runnersUp.add(sorted[1])
            }
        }
        return Pair(winners, runnersUp)
    }
}
