package com.duallive.app.ucl2026.viewmodel

import androidx.lifecycle.ViewModel
import com.duallive.app.ucl2026.model.Ucl26StandingRow
import com.duallive.app.ucl2026.model.Ucl26Match
import com.duallive.app.ucl2026.model.BracketMatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class Ucl26ViewModel : ViewModel() {
    // Swiss League State
    private val _standings = MutableStateFlow<List<Ucl26StandingRow>>(emptyList())
    val standings: StateFlow<List<Ucl26StandingRow>> = _standings

    private val _matches = MutableStateFlow<List<Ucl26Match>>(emptyList())
    val matches: StateFlow<List<Ucl26Match>> = _matches

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound

    // Knockout / Bracket State
    private val _bracketMatches = MutableStateFlow<List<BracketMatch>>(emptyList())
    val bracketMatches: StateFlow<List<BracketMatch>> = _bracketMatches

    fun initializeTournament(names: List<String>) {
        val initialTeams = names.mapIndexed { index, name ->
            Ucl26StandingRow(teamId = index + 1, teamName = name)
        }
        _standings.value = initialTeams
        generateRound(1)
    }

    private fun generateRound(roundNumber: Int) {
        val sortedTeams = _standings.value.sortedWith(
            compareByDescending<Ucl26StandingRow> { it.points }
                .thenByDescending { it.goalDifference }
        )
        
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
            var pts = 0; var mp = 0; var gd = 0
            playedMatches.forEach { m ->
                if (m.homeTeamId == team.teamId) {
                    mp++; gd += (m.homeScore!! - m.awayScore!!)
                    if (m.homeScore!! > m.awayScore!!) pts += 3
                    else if (m.homeScore!! == m.awayScore!!) pts += 1
                }
                if (m.awayTeamId == team.teamId) {
                    mp++; gd += (m.awayScore!! - m.homeScore!!)
                    if (m.awayScore!! > m.homeScore!!) pts += 3
                    else if (m.awayScore!! == m.homeScore!!) pts += 1
                }
            }
            team.copy(matchesPlayed = mp, points = pts, goalDifference = gd)
        }.sortedWith(
            compareByDescending<Ucl26StandingRow> { it.points }
                .thenByDescending { it.goalDifference }
        )

        _standings.value = updatedTeams
    }

    fun nextRound() {
        if (_currentRound.value < 8) {
           _currentRound.value += 1
            generateRound(_currentRound.value)
        } else {
            generateQuarterFinals()
        }
    }

    private fun generateQuarterFinals() {
        val top8 = _standings.value.take(8)
        if (top8.size < 8) return

        val pairings = listOf(0 to 7, 1 to 6, 2 to 5, 3 to 4)
        val qfMatches = pairings.mapIndexed { index, (homeIdx, awayIdx) ->
            BracketMatch(
                id = index + 1,
                roundName = "Quarter-Final",
                team1Name = top8[homeIdx].teamName,
                team2Name = top8[awayIdx].teamName
            )
        }
        _bracketMatches.value = qfMatches
    }

    fun generateSemiFinals() {
        val qfResults = _bracketMatches.value.filter { it.roundName == "Quarter-Final" && it.isCompleted }
        if (qfResults.size == 4) {
            // Determine winners based on aggregate
            val winners = qfResults.map { if (it.aggregate1 > it.aggregate2) it.team1Name else it.team2Name }
            val sfMatches = listOf(
                BracketMatch(id = 5, roundName = "Semi-Final", team1Name = winners[0], team2Name = winners[1]),
                BracketMatch(id = 6, roundName = "Semi-Final", team1Name = winners[2], team2Name = winners[3])
            )
            _bracketMatches.update { it + sfMatches }
        }
    }

    fun generateFinal() {
        val sfResults = _bracketMatches.value.filter { it.roundName == "Semi-Final" && it.isCompleted }
        if (sfResults.size == 2) {
            val winners = sfResults.map { if (it.aggregate1 > it.aggregate2) it.team1Name else it.team2Name }
            val finalMatch = BracketMatch(
                id = 7, 
                roundName = "FINAL", 
                team1Name = winners[0], 
                team2Name = winners[1]
            )
            _bracketMatches.update { it + finalMatch }
        }
    }

    fun updateBracketScore(
        matchId: Int, 
        leg1: Pair<Int, Int>? = null, 
        leg2: Pair<Int, Int>? = null
    ) {
        _bracketMatches.update { current ->
            current.map { match ->
                if (match.id == matchId) {
                    val newLeg1Score1 = leg1?.first ?: match.leg1Score1
                    val newLeg1Score2 = leg1?.second ?: match.leg1Score2
                    val newLeg2Score1 = leg2?.first ?: match.leg2Score1
                    val newLeg2Score2 = leg2?.second ?: match.leg2Score2
                    
                    match.copy(
                        leg1Score1 = newLeg1Score1,
                        leg1Score2 = newLeg1Score2,
                        leg2Score1 = newLeg2Score1,
                        leg2Score2 = newLeg2Score2,
                        // Match is completed if both legs are entered OR if it is the Final (single leg)
                        isCompleted = if (match.roundName == "FINAL") {
                            newLeg1Score1 != null && newLeg1Score2 != null
                        } else {
                            newLeg1Score1 != null && newLeg1Score2 != null && newLeg2Score1 != null && newLeg2Score2 != null
                        }
                    )
                } else match
            }
        }
    }
}
