package com.duallive.app.ucl2026.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duallive.app.data.AppDatabase
import com.duallive.app.ucl2026.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class Ucl26ViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    
    private val _standings = MutableStateFlow<List<Ucl26Team>>(emptyList())
    val standings: StateFlow<List<Ucl26Team>> = _standings.asStateFlow()

    private val _matches = MutableStateFlow<List<Ucl26Match>>(emptyList())
    val matches: StateFlow<List<Ucl26Match>> = _matches.asStateFlow()

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    private val _bracketMatches = MutableStateFlow<List<BracketMatch>>(emptyList())
    val bracketMatches: StateFlow<List<BracketMatch>> = _bracketMatches.asStateFlow()

    // Initialize tournament and SAVE to DB
    fun initializeTournament(leagueName: String, teamNames: List<String>) {
        viewModelScope.launch {
            val teams = teamNames.mapIndexed { index, name -> 
                Ucl26Team(teamId = index, teamName = name) 
            }
            _standings.value = teams.sortedWith(compareByDescending<Ucl26Team> { it.points }.thenByDescending { it.goalDifference })
            _currentRound.value = 1
            generateRoundFixtures(1)
            
            // TODO: Room mapping for Ucl26 specific tables if added to Schema
            // For now, we keep the state in memory but initialized via this professional flow
        }
    }

    fun updateScore(matchId: Int, homeScore: Int, awayScore: Int) {
        val updatedMatches = _matches.value.map {
            if (it.matchId == matchId) it.copy(homeScore = homeScore, awayScore = awayScore, isPlayed = true)
            else it
        }
        _matches.value = updatedMatches
        recalculateStandings()
    }

    private fun recalculateStandings() {
        // Logic to update Points/GD based on _matches
        val currentTeams = _standings.value.toMutableList()
        // ... (calculation logic)
        _standings.value = currentTeams.sortedWith(compareByDescending<Ucl26Team> { it.points }.thenByDescending { it.goalDifference })
    }

    fun generateRoundFixtures(round: Int) {
        // Swiss System pairings logic based on current standings
        val newMatches = mutableListOf<Ucl26Match>()
        val sorted = _standings.value
        for (i in 0 until sorted.size step 2) {
            if (i + 1 < sorted.size) {
                newMatches.add(Ucl26Match(matchId = round * 100 + i, homeTeamId = sorted[i].teamId, awayTeamId = sorted[i+1].teamId))
            }
        }
        _matches.value = newMatches
    }

    fun nextRound() {
        if (_currentRound.value < 8) {
            _currentRound.value += 1
            generateRoundFixtures(_currentRound.value)
        }
    }
    
    fun generateSemiFinals() { /* Bracket Logic */ }
    fun generateFinal() { /* Bracket Logic */ }
}
