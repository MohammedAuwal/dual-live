package com.duallive.app.ucl2026.logic

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import com.duallive.app.ucl2026.model.Ucl26StandingRow

class Ucl26ViewModel : ViewModel() {
    // This holds our official 36 teams once confirmed
    val standings = mutableStateListOf<Ucl26StandingRow>()

    fun startTournament(teamNames: List<String>) {
        standings.clear()
        teamNames.forEachIndexed { index, name ->
            standings.add(
                Ucl26StandingRow(
                    teamId = index + 1,
                    teamName = name,
                    matchesPlayed = 0,
                    points = 0
                )
            )
        }
    }
}
