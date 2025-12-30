package com.duallive.app.utils

import com.duallive.app.data.entity.Match
import com.duallive.app.data.entity.Standing
import com.duallive.app.data.entity.Team

object TableCalculator {
    fun calculate(teams: List<Team>, matches: List<Match>): List<Standing> {
        val standingsMap = teams.associate { it.id to Standing(teamId = it.id) }.toMutableMap()

        for (match in matches) {
            val home = standingsMap[match.homeTeamId] ?: continue
            val away = standingsMap[match.awayTeamId] ?: continue

            // 1. Calculate base updates
            var h = home.copy(
                matchesPlayed = home.matchesPlayed + 1,
                goalsFor = home.goalsFor + match.homeScore,
                goalsAgainst = home.goalsAgainst + match.awayScore
            )
            var a = away.copy(
                matchesPlayed = away.matchesPlayed + 1,
                goalsFor = away.goalsFor + match.awayScore,
                goalsAgainst = away.goalsAgainst + match.homeScore
            )

            // 2. Calculate Outcome (Points)
            when {
                match.homeScore > match.awayScore -> {
                    h = h.copy(wins = h.wins + 1, points = h.points + 3)
                    a = a.copy(losses = a.losses + 1)
                }
                match.homeScore < match.awayScore -> {
                    a = a.copy(wins = a.wins + 1, points = a.points + 3)
                    h = h.copy(losses = h.losses + 1)
                }
                else -> {
                    h = h.copy(draws = h.draws + 1, points = h.points + 1)
                    a = a.copy(draws = a.draws + 1, points = a.points + 1)
                }
            }
            
            standingsMap[match.homeTeamId] = h
            standingsMap[match.awayTeamId] = a
        }

        // 3. Sorting: Points -> Goal Difference -> Goals For
        return standingsMap.values.toList().sortedWith(
            compareByDescending<Standing> { it.points }
                .thenByDescending { it.goalsFor - it.goalsAgainst }
                .thenByDescending { it.goalsFor }
        )
    }

    /**
     * Safety Check for UCL:
     * Calculates if all required matches have been played.
     */
    fun isGroupStageComplete(teams: List<Team>, matches: List<Match>, isHomeAndAway: Boolean = false): Boolean {
        if (teams.isEmpty()) return false
        
        // Group size usually 4. Total games = (N * (N-1) / 2)
        // If Home & Away, multiply by 2.
        val baseMatches = (teams.size * (teams.size - 1)) / 2
        val totalNeeded = if (isHomeAndAway) baseMatches * 2 else baseMatches
        
        return matches.size >= totalNeeded && matches.size > 0
    }
}
