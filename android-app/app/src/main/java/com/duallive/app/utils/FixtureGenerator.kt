package com.duallive.app.utils

import com.duallive.app.data.entity.Team

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
    
    // For Classic League - ROUND ROBIN
    fun generateRoundRobin(teams: List<Team>, homeAndAway: Boolean): List<Fixture> {
        if (teams.size < 2) return emptyList()
        
        val teamList = teams.toMutableList()
        val hasBye = teamList.size % 2 != 0
        if (hasBye) {
            teamList.add(Team(id = -1, leagueId = -1, name = "BYE"))
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

                if (homeTeam.id != -1 && awayTeam.id != -1) {
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
    
    // For UCL - GROUP STAGE (per group)
    fun generateUCLGroupFixtures(groupTeams: List<Team>, homeAndAway: Boolean): List<Fixture> {
        if (groupTeams.size < 2) return emptyList()
        
        val teamList = groupTeams.toMutableList()
        val hasBye = teamList.size % 2 != 0
        if (hasBye) {
            teamList.add(Team(id = -1, leagueId = -1, name = "BYE"))
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

                if (homeTeam.id != -1 && awayTeam.id != -1) {
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

    // For both: Simple knockout draw
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

    // UCL SPECIFIC: Round of 16 Draw with group protection
    fun generateUCLRoundOf16Draw(groups: Map<String?, List<Team>>): List<Fixture> {
        val fixtures = mutableListOf<Fixture>()
        
        val groupWinners = mutableListOf<Team>()
        val groupRunnersUp = mutableListOf<Team>()
        
        // Sort groups A-H and take top 2 from each
        for (group in 'A'..'H') {
            val groupKey = group.toString()
            val teams = groups[groupKey] ?: continue
            if (teams.size >= 2) {
                groupWinners.add(teams[0])
                groupRunnersUp.add(teams[1])
            }
        }
        
        // Shuffle runners-up and match with winners (avoid same group)
        val shuffledRunnersUp = groupRunnersUp.shuffled().toMutableList()
        
        for ((index, winner) in groupWinners.withIndex()) {
            var runnerUp: Team? = null
            var runnerUpIndex = 0
            
            for (i in shuffledRunnersUp.indices) {
                if (shuffledRunnersUp[i].groupName != winner.groupName) {
                    runnerUp = shuffledRunnersUp[i]
                    runnerUpIndex = i
                    break
                }
            }
            
            if (runnerUp != null) {
                fixtures.add(Fixture(
                    round = 1,
                    homeTeam = winner,
                    awayTeam = runnerUp,
                    label = "Round of 16",
                    stage = "RO16",
                    matchNumber = index + 1,
                    isKnockout = true
                ))
                shuffledRunnersUp.removeAt(runnerUpIndex)
            }
        }
        
        return fixtures
    }
    
    // Generate next knockout round from winners
    fun generateNextKnockoutRound(previousRoundWinners: List<Team>, stage: String): List<Fixture> {
        val fixtures = mutableListOf<Fixture>()
        val shuffled = previousRoundWinners.shuffled()
        
        for (i in 0 until (shuffled.size / 2) * 2 step 2) {
            fixtures.add(Fixture(
                round = 1,
                homeTeam = shuffled[i],
                awayTeam = shuffled[i+1],
                label = stage,
                stage = stage,
                matchNumber = i/2 + 1,
                isKnockout = true
            ))
        }
        
        return fixtures
    }
    
    // Get winners from completed knockout matches
    fun getWinnersFromKnockoutRound(
        matches: List<com.duallive.app.data.entity.Match>,
        teams: List<Team>,
        stage: String
    ): List<Team> {
        val winners = mutableListOf<Team>()
        val teamMap = teams.associateBy { it.id }
        
        for (match in matches.filter { it.stage == stage }) {
            val homeTeam = teamMap[match.homeTeamId]
            val awayTeam = teamMap[match.awayTeamId]
            
            if (homeTeam != null && awayTeam != null) {
                when {
                    match.homeScore > match.awayScore -> winners.add(homeTeam)
                    match.awayScore > match.homeScore -> winners.add(awayTeam)
                    else -> winners.add(homeTeam) // Home team advantage for draws
                }
            }
        }
        
        return winners
    }
    
    // Check if group stage is complete (for UCL)
    fun isUCLGroupStageComplete(matches: List<com.duallive.app.data.entity.Match>): Boolean {
        val groupMatches = matches.filter { it.stage == "GROUP" }
        // 8 groups × 6 matchdays × 4 matches per matchday = 192 matches
        return groupMatches.size >= 192
    }
    
    // Get top teams from standings for UCL knockout
    fun getTopTeamsForUCLKnockout(teams: List<Team>, standings: Map<Team, Int>): Pair<List<Team>, List<Team>> {
        val groupWinners = mutableListOf<Team>()
        val groupRunnersUp = mutableListOf<Team>()
        
        val groupedTeams = teams.groupBy { it.groupName }
        
        for ((groupName, groupTeams) in groupedTeams) {
            if (groupName != null && groupName in "ABCDEFGH") {
                val sortedTeams = groupTeams.sortedByDescending { standings[it] ?: 0 }
                if (sortedTeams.isNotEmpty()) groupWinners.add(sortedTeams[0])
                if (sortedTeams.size >= 2) groupRunnersUp.add(sortedTeams[1])
            }
        }
        
        return Pair(groupWinners, groupRunnersUp)
    }
}
