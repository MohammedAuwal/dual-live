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

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound

    fun initializeTournament(names: List<String>) {
        val initialTeams = names.mapIndexed { index, name ->
            Ucl26StandingRow(teamId = index + 1, teamName = name)
        }
        _standings.value = initialTeams
        generateRound(1)
    }

    private fun generateRound(roundNumber: Int) {
        val sortedTeams = _standings.value.sortedByDescending { it.points }
        val newMatches = mutableListOf<Ucl26Match>()
        val pairedIds = mutableSetOf<Int>()

        for (i in sortedTeams.indices) {
            val homeTeam = sortedTeams[i]
            if (homeTeam.teamId in pairedIds) continue

            for (j in i + 1 until sortedTeams.size) {
                val awayTeam = sortedTeams[j]
                if (awayTeam.teamId in pairedIds) continue
                
                newMatches.add(
                    Ucl26Match(
                        matchId = _matches.value.size + newMatches.size + 1,
                        homeTeamId = homeTeam.teamId,
                        awayTeamId = awayTeam.teamId
                    )
                )
                pairedIds.add(homeTeam.teamId)
                pairedIds.add(awayTeam.teamId)
                break
            }
        }
        _matches.update { it + newMatches }
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
                    else if (m.awayScore!! == m.homeScore!!) pts += 1
                }
            }
            team.copy(matchesPlayed = mp, points = pts, goalDifference = gd)
        }.sortedWith(compareByDescending<Ucl26StandingRow> { it.points }.thenByDescending { it.goalDifference })

        _standings.value = updatedTeams
    }

    fun nextRound() {
        if (_currentRound.value < 8) {
            _currentRound.value += 1
            generateRound(_currentRound.value)
        }
    }
}
