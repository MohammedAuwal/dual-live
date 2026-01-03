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

    private val _bracketMatches = MutableStateFlow<List<BracketMatch>>(emptyList())
    val bracketMatches: StateFlow<List<BracketMatch>> = _bracketMatches.asStateFlow()

    private val _r16Matches = MutableStateFlow<List<BracketMatch>>(emptyList())
    val r16Matches: StateFlow<List<BracketMatch>> = _r16Matches.asStateFlow()

    fun generatePlayoffDraw() {
        val sorted = _standings.value.sortedByDescending { it.points }
        val playoffTeams = sorted.subList(8, 24)
        val pairings = mutableListOf<BracketMatch>()
        for (i in 0 until 8) {
            pairings.add(BracketMatch(i, playoffTeams[i].teamName, playoffTeams[15-i].teamName, 0, 0))
        }
        _bracketMatches.value = pairings
    }

    // NEW: Logic to draw R16 (Top 8 vs Play-off Winners)
    fun generateR16Draw() {
        val winners = _bracketMatches.value.map { match ->
            if (match.aggregate1 >= match.aggregate2) match.team1Name else match.team2Name
        }.shuffled() // Shuffle winners for excitement!

        val top8 = _standings.value.sortedByDescending { it.points }.take(8).shuffled()
        
        val r16Pairings = mutableListOf<BracketMatch>()
        for (i in 0 until 8) {
            r16Pairings.add(BracketMatch(i + 100, top8[i].teamName, winners[i], 0, 0))
        }
        _r16Matches.value = r16Pairings
    }
}
