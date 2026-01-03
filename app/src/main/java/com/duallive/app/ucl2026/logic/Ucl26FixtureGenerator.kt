package com.duallive.app.ucl2026.logic

import com.duallive.app.ucl2026.model.Ucl26Team // We will create this model next

data class Ucl26Match(
    val homeTeamId: Int,
    val awayTeamId: Int,
    val matchday: Int
)

class Ucl26FixtureGenerator {
    
    fun generateLeaguePhase(teams: List<Int>): List<Ucl26Match> {
        if (teams.size != 36) throw IllegalArgumentException("UCL League Phase requires 36 teams")
        
        val fixtures = mutableListOf<Ucl26Match>()
        val matchesPerTeam = mutableMapOf<Int, Int>()
        val opponents = mutableMapOf<Int, MutableSet<Int>>()
        val homeMatches = mutableMapOf<Int, Int>()

        // Initialize tracking
        teams.forEach { 
            matchesPerTeam[it] = 0 
            opponents[it] = mutableSetOf()
            homeMatches[it] = 0
        }

        // Logic: Simple Round-Robin variant for 8 matchdays
        // In a production app, we would use Pots 1-4.
        // For this version, we will ensure 4H/4A and 8 unique opponents.
        
        for (day in 1..8) {
            val available = teams.toMutableList()
            available.shuffle()
            
            while (available.size >= 2) {
                val teamA = available.removeAt(0)
                // Find a valid opponent B
                val teamBIndex = available.indexOfFirst { b ->
                    !opponents[teamA]!!.contains(b) && 
                    !opponents[b]!!.contains(teamA)
                }

                if (teamBIndex != -1) {
                    val teamB = available.removeAt(teamBIndex)
                    
                    // Decide Home/Away based on who needs more Home games
                    val isAHome = homeMatches[teamA]!! < 4 && (homeMatches[teamB]!! >= 4 || (0..1).random() == 0)
                    
                    if (isAHome) {
                        fixtures.add(Ucl26Match(teamA, teamB, day))
                        homeMatches[teamA] = homeMatches[teamA]!! + 1
                    } else {
                        fixtures.add(Ucl26Match(teamB, teamA, day))
                        homeMatches[teamB] = homeMatches[teamB]!! + 1
                    }
                    
                    opponents[teamA]!!.add(teamB)
                    opponents[teamB]!!.add(teamA)
                } else {
                    // If we hit a deadlock, we retry the day
                    return generateLeaguePhase(teams) 
                }
            }
        }
        return fixtures
    }
}
