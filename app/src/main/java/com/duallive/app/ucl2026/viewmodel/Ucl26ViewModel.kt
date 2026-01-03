package com.duallive.app.ucl2026.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duallive.app.ucl2026.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class Ucl26ViewModel(application: Application) : AndroidViewModel(application) {
    private val _standings = MutableStateFlow<List<Ucl26Team>>(emptyList())
    val standings: StateFlow<List<Ucl26Team>> = _standings.asStateFlow()

    private val _matches = MutableStateFlow<List<Ucl26Match>>(emptyList())
    val matches: StateFlow<List<Ucl26Match>> = _matches.asStateFlow()

    private val _bracketMatches = MutableStateFlow<List<BracketMatch>>(emptyList())
    val bracketMatches: StateFlow<List<BracketMatch>> = _bracketMatches.asStateFlow()

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    fun initializeTournament(teamNames: List<String>) {
        val teams = teamNames.mapIndexed { index, name -> 
            Ucl26Team(teamId = index, teamName = name) 
        }
        _standings.value = teams
        generateRoundFixtures(1)
    }

    // --- SWISS SYSTEM DRAW ---
    fun generateRoundFixtures(round: Int) {
        val sorted = _standings.value.sortedByDescending { it.points }
        val newMatches = mutableListOf<Ucl26Match>()
        for (i in 0 until sorted.size step 2) {
            if (i + 1 < sorted.size) {
                newMatches.add(Ucl26Match(matchId = round * 100 + i, homeTeamId = sorted[i].teamId, awayTeamId = sorted[i+1].teamId))
            }
        }
        _matches.value = newMatches
    }

    // --- KNOCKOUT PLAY-OFF DRAW (9th-24th) ---
    fun generatePlayoffDraw() {
        val sorted = _standings.value.sortedByDescending { it.points }
        // Teams 9th (index 8) to 24th (index 23)
        val playoffTeams = sorted.subList(8, 24)
        val pairings = mutableListOf<BracketMatch>()
        
        // UEFA Logic: 9 vs 24, 10 vs 23, 11 vs 22...
        for (i in 0 until 8) {
            val seeded = playoffTeams[i]          // High rank (9th, 10th...)
            val unseeded = playoffTeams[15 - i]   // Low rank (24th, 23rd...)
            pairings.add(BracketMatch(
                id = i,
                team1Name = seeded.teamName,
                team2Name = unseeded.teamName,
                aggregate1 = 0,
                aggregate2 = 0
            ))
        }
        _bracketMatches.value = pairings
    }

    fun updateScore(matchId: Int, homeScore: Int, awayScore: Int) {
        _matches.value = _matches.value.map {
            if (it.matchId == matchId) it.copy(homeScore = homeScore, awayScore = awayScore, isPlayed = true)
            else it
        }
    }
}
