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

            val updatedHome = home.copy(matchesPlayed = home.matchesPlayed + 1)
            val updatedAway = away.copy(matchesPlayed = away.matchesPlayed + 1)

            when {
                match.homeScore > match.awayScore -> {
                    standingsMap[match.homeTeamId] = updatedHome.copy(wins = home.wins + 1, points = home.points + 3)
                    standingsMap[match.awayTeamId] = updatedAway.copy(losses = away.losses + 1)
                }
                match.homeScore < match.awayScore -> {
                    standingsMap[match.awayTeamId] = updatedAway.copy(wins = away.wins + 1, points = away.points + 3)
                    standingsMap[match.homeTeamId] = updatedHome.copy(losses = home.losses + 1)
                }
                else -> {
                    standingsMap[match.homeTeamId] = updatedHome.copy(draws = home.draws + 1, points = home.points + 1)
                    standingsMap[match.awayTeamId] = updatedAway.copy(draws = away.draws + 1, points = away.points + 1)
                }
            }
        }
        return standingsMap.values.toList().sortedByDescending { it.points }
    }
}
