package com.duallive.app.ucl2026.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.duallive.app.ucl2026.model.*
import kotlinx.coroutines.flow.*

class Ucl26ViewModel(application: Application) : AndroidViewModel(application) {
    private val _teams = MutableStateFlow<List<Ucl26Team>>(emptyList())
    val teams: StateFlow<List<Ucl26Team>> = _teams.asStateFlow()

    private val _matches = MutableStateFlow<List<Ucl26Match>>(emptyList())
    val matches: StateFlow<List<Ucl26Match>> = _matches.asStateFlow()

    private val _bracketMatches = MutableStateFlow<List<BracketMatch>>(emptyList())
    val bracketMatches: StateFlow<List<BracketMatch>> = _bracketMatches.asStateFlow()

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    // Automatic Standings Calculation
    val standings: StateFlow<List<Ucl26Team>> = combine(_teams, _matches) { teams, matches ->
        val statsMap = teams.associateBy({ it.teamId }, { it.copy(points = 0, goalsScored = 0, goalsConceded = 0, matchesPlayed = 0) }).toMutableMap()
        matches.filter { it.isPlayed }.forEach { match ->
            val home = statsMap[match.homeTeamId]
            val away = statsMap[match.awayTeamId]
            if (home != null && away != null) {
                statsMap[match.homeTeamId] = home.addMatchResults(match.homeScore, match.awayScore)
                statsMap[match.awayTeamId] = away.addMatchResults(match.awayScore, match.homeScore)
            }
        }
        statsMap.values.sortedWith(compareByDescending<Ucl26Team> { it.points }.thenByDescending { it.goalDifference }.thenByDescending { it.goalsScored })
    }.stateIn(scope = kotlinx.coroutines.MainScope(), started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    private fun Ucl26Team.addMatchResults(scored: Int, conceded: Int): Ucl26Team = this.copy(
        matchesPlayed = this.matchesPlayed + 1,
        goalsScored = this.goalsScored + scored,
        goalsConceded = this.goalsConceded + conceded,
        points = this.points + when { scored > conceded -> 3; scored == conceded -> 1; else -> 0 }
    )

    fun initializeTournament(teamNames: List<String>) {
        val initialTeams = teamNames.mapIndexed { index, name -> Ucl26Team(teamId = index, teamName = name) }
        _teams.value = initialTeams
        generateRoundFixtures(1)
    }

    fun updateScore(matchId: Int, homeScore: Int, awayScore: Int) {
        _matches.value = _matches.value.map {
            if (it.matchId == matchId) it.copy(homeScore = homeScore, awayScore = awayScore, isPlayed = (homeScore != 0 || awayScore != 0))
            else it
        }
    }

    // --- NEW: BRACKET SCORE UPDATE ---
    fun updateBracketScore(matchId: Int, s1: Int, s2: Int) {
        _bracketMatches.value = _bracketMatches.value.map {
            if (it.id == matchId) {
                // Update leg 1 scores (Standard for simple knockout)
                it.copy(leg1Score1 = s1, leg1Score2 = s2, isCompleted = (s1 != s2))
            } else it
        }
    }

    fun generateRoundFixtures(round: Int) {
        val currentStandings = _standingsManual() 
        val newMatches = mutableListOf<Ucl26Match>()
        for (i in 0 until currentStandings.size step 2) {
            if (i + 1 < currentStandings.size) {
                newMatches.add(Ucl26Match(matchId = round * 100 + i, homeTeamId = currentStandings[i].teamId, awayTeamId = currentStandings[i+1].teamId))
            }
        }
        _matches.value = _matches.value + newMatches
    }

    private fun _standingsManual(): List<Ucl26Team> {
        // Quick helper for internal fixture generation
        return _teams.value.sortedByDescending { it.points } 
    }

    fun nextRound() {
        if (_currentRound.value < 8) {
            _currentRound.value += 1
            generateRoundFixtures(_currentRound.value)
        } else if (_currentRound.value == 8) {
            generatePlayoffDraw()
        }
    }

    fun generatePlayoffDraw() {
        val sorted = _standingsManual()
        if (sorted.size >= 24) {
            val playoffPairs = mutableListOf<BracketMatch>()
            val seeds = sorted.subList(8, 24)
            for (i in 0 until 8) {
                playoffPairs.add(BracketMatch(id = 200 + i, roundName = "R16", team1Name = seeds[i].teamName, team2Name = seeds[15-i].teamName))
            }
            _bracketMatches.value = playoffPairs
        }
    }
}
