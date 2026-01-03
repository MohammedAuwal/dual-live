package com.duallive.app.ucl2026.viewmodel

import androidx.lifecycle.ViewModel
import com.duallive.app.ucl2026.model.Ucl26StandingRow
import com.duallive.app.ucl2026.model.Ucl26Match
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class Ucl26ViewModel : ViewModel() {
    private val _standings = MutableStateFlow<List<Ucl26StandingRow>>(emptyList())
    val standings: StateFlow<List<Ucl26StandingRow>> = _standings

    private val _matches = MutableStateFlow<List<Ucl26Match>>(emptyList())
    val matches: StateFlow<List<Ucl26Match>> = _matches

    fun initializeTournament(names: List<String>) {
        val initialTeams = names.mapIndexed { index, name ->
            Ucl26StandingRow(teamId = index + 1, teamName = name)
        }
        _standings.value = initialTeams
        
        // Example: Create some dummy matches for Round 1
        // In a real Swiss system, you'd generate these based on rank
        val initialMatches = mutableListOf<Ucl26Match>()
        for (i in 0 until names.size step 2) {
            if (i + 1 < names.size) {
                initialMatches.add(Ucl26Match(matchId = i, homeTeamId = i + 1, awayTeamId = i + 2))
            }
        }
        _matches.value = initialMatches
    }

    fun updateScore(matchId: Int, homeScore: Int, awayScore: Int) {
        _matches.update { currentMatches ->
            currentMatches.map { match ->
                if (match.matchId == matchId) {
                    match.copy(homeScore = homeScore, awayScore = awayScore, isPlayed = true)
                } else match
            }
        }
        recalculateTable()
    }

    private fun recalculateTable() {
        val currentTeams = _standings.value.map { it.copy(matchesPlayed = 0, points = 0, goalDifference = 0) }
        val playedMatches = _matches.value.filter { it.isPlayed }

        val updatedTeams = currentTeams.map { team ->
            var pts = 0
            var mp = 0
            var gd = 0
            
            playedMatches.forEach { m ->
                if (m.homeTeamId == team.teamId) {
                    mp++
                    gd += (m.homeScore!! - m.awayScore!!)
                    if (m.homeScore!! > m.awayScore!!) pts += 3
                    else if (m.homeScore!! == m.awayScore!!) pts += 1
                }
                if (m.awayTeamId == team.teamId) {
                    mp++
                    gd += (m.awayScore!! - m.homeScore!!)
                    if (m.awayScore!! > m.homeScore!!) pts += 3
                    else if (m.homeScore!! == m.awayScore!!) pts += 1
                }
            }
            team.copy(matchesPlayed = mp, points = pts, goalDifference = gd)
        }.sortedWith(compareByDescending<Ucl26StandingRow> { it.points }.thenByDescending { it.goalDifference })

        _standings.value = updatedTeams
    }
}
