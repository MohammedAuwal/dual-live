package com.duallive.app.ucl2026.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.duallive.app.ucl2026.model.*
import kotlinx.coroutines.flow.*

class Ucl26ViewModel(application: Application) : AndroidViewModel(application) {
    private val _standings = MutableStateFlow<List<Ucl26Team>>(emptyList())
    val standings: StateFlow<List<Ucl26Team>> = _standings.asStateFlow()

    private val _matches = MutableStateFlow<List<Ucl26Match>>(emptyList())
    val matches: StateFlow<List<Ucl26Match>> = _matches.asStateFlow()

    private val _bracketMatches = MutableStateFlow<List<BracketMatch>>(emptyList())
    val bracketMatches: StateFlow<List<BracketMatch>> = _bracketMatches.asStateFlow()

    private val _r16Matches = MutableStateFlow<List<BracketMatch>>(emptyList())
    val r16Matches: StateFlow<List<BracketMatch>> = _r16Matches.asStateFlow()

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    fun initializeTournament(teamNames: List<String>) {
        val teams = teamNames.mapIndexed { index, name -> 
            Ucl26Team(teamId = index, teamName = name) 
        }
        _standings.value = teams
        generateRoundFixtures(1)
    }

    fun generateRoundFixtures(round: Int) {
        val sorted = _standings.value.sortedByDescending { it.points }
        val newMatches = mutableListOf<Ucl26Match>()
        for (i in 0 until sorted.size step 2) {
            if (i + 1 < sorted.size) {
                newMatches.add(Ucl26Match(
                    matchId = round * 100 + i, 
                    homeTeamId = sorted[i].teamId, 
                    awayTeamId = sorted[i+1].teamId
                ))
            }
        }
        _matches.value = newMatches
    }

    fun updateScore(matchId: Int, homeScore: Int, awayScore: Int) {
        _matches.value = _matches.value.map {
            if (it.matchId == matchId) it.copy(homeScore = homeScore, awayScore = awayScore, isPlayed = true)
            else it
        }
    }

    fun nextRound() {
        if (_currentRound.value < 8) {
            _currentRound.value += 1
            generateRoundFixtures(_currentRound.value)
        }
    }

    fun generatePlayoffDraw() {
        val sorted = _standings.value.sortedByDescending { it.points }
        if (sorted.size >= 24) {
            val playoffTeams = sorted.subList(8, 24)
            val pairings = mutableListOf<BracketMatch>()
            for (i in 0 until 8) {
                pairings.add(BracketMatch(i, playoffTeams[i].teamName, playoffTeams[15-i].teamName, 0, 0))
            }
            _bracketMatches.value = pairings
        }
    }

    fun generateR16Draw() {
        val winners = _bracketMatches.value.map { if (it.aggregate1 >= it.aggregate2) it.team1Name else it.team2Name }.shuffled()
        val top8 = _standings.value.sortedByDescending { it.points }.take(8).shuffled()
        val r16Pairings = mutableListOf<BracketMatch>()
        for (i in 0 until 8) {
            r16Pairings.add(BracketMatch(i + 100, top8[i].teamName, winners[i], 0, 0))
        }
        _r16Matches.value = r16Pairings
    }
}
