package com.duallive.app.ucl2026.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.duallive.app.ucl2026.model.*
import kotlinx.coroutines.flow.*

class Ucl26ViewModel(application: Application) : AndroidViewModel(application) {
    // Primary Data Sources
    private val _teams = MutableStateFlow<List<Ucl26Team>>(emptyList())
    val teams: StateFlow<List<Ucl26Team>> = _teams.asStateFlow()

    private val _matches = MutableStateFlow<List<Ucl26Match>>(emptyList())
    val matches: StateFlow<List<Ucl26Match>> = _matches.asStateFlow()

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    // Standings are derived automatically from matches to ensure they are ALWAYS up to date
    val standings: StateFlow<List<Ucl26Team>> = combine(_teams, _matches) { teams, matches ->
        calculateStandings(teams, matches)
    }.stateIn(
        scope = kotlinx.coroutines.MainScope(),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun initializeTournament(teamNames: List<String>) {
        val initialTeams = teamNames.mapIndexed { index, name -> 
            Ucl26Team(teamId = index, teamName = name) 
        }
        _teams.value = initialTeams
        generateRoundFixtures(1)
    }

    fun updateScore(matchId: Int, homeScore: Int, awayScore: Int) {
        _matches.value = _matches.value.map {
            if (it.matchId == matchId) {
                // If it's 0-0, we treat it as unplayed unless you manually change it
                val played = !(homeScore == 0 && awayScore == 0)
                it.copy(homeScore = homeScore, awayScore = awayScore, isPlayed = played)
            } else it
        }
    }

    private fun calculateStandings(teams: List<Ucl26Team>, matches: List<Ucl26Match>): List<Ucl26Team> {
        val statsMap = teams.associateBy({ it.teamId }, { it.copy(points = 0, goalsScored = 0, goalsConceded = 0, matchesPlayed = 0) }).toMutableMap()
        
        matches.filter { it.isPlayed }.forEach { match ->
            val home = statsMap[match.homeTeamId]
            val away = statsMap[match.awayTeamId]

            if (home != null && away != null) {
                statsMap[match.homeTeamId] = home.addMatchResults(match.homeScore, match.awayScore)
                statsMap[match.awayTeamId] = away.addMatchResults(match.awayScore, match.homeScore)
            }
        }
        
        return statsMap.values.sortedWith(
            compareByDescending<Ucl26Team> { it.points }
                .thenByDescending { it.goalDifference }
                .thenByDescending { it.goalsScored }
        )
    }

    private fun Ucl26Team.addMatchResults(scored: Int, conceded: Int): Ucl26Team {
        val pts = when {
            scored > conceded -> 3
            scored == conceded -> 1
            else -> 0
        }
        return this.copy(
            matchesPlayed = this.matchesPlayed + 1,
            goalsScored = this.goalsScored + scored,
            goalsConceded = this.goalsConceded + conceded,
            points = this.points + pts
        )
    }

    fun generateRoundFixtures(round: Int) {
        // We use the current calculated standings to pair teams
        val currentStandings = calculateStandings(_teams.value, _matches.value)
        val newMatches = mutableListOf<Ucl26Match>()
        for (i in 0 until currentStandings.size step 2) {
            if (i + 1 < currentStandings.size) {
                newMatches.add(Ucl26Match(
                    matchId = round * 100 + i, 
                    homeTeamId = currentStandings[i].teamId, 
                    awayTeamId = currentStandings[i+1].teamId
                ))
            }
        }
        // Use plus to keep old matches and add new ones
        _matches.value = _matches.value + newMatches
    }

    fun nextRound() {
        if (_currentRound.value < 8) {
            _currentRound.value += 1
            generateRoundFixtures(_currentRound.value)
        }
    }
}
