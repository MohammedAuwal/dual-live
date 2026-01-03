package com.duallive.app.ucl2026.model

data class Ucl26RegistrationState(
    val selectedTeams: List<Ucl26Team> = emptyList(),
    val isRegistrationComplete: Boolean = false
) {
    val teamsInPot1 = selectedTeams.filter { it.pot == 1 }
    val teamsInPot2 = selectedTeams.filter { it.pot == 2 }
    val teamsInPot3 = selectedTeams.filter { it.pot == 3 }
    val teamsInPot4 = selectedTeams.filter { it.pot == 4 }
    
    val totalCount = selectedTeams.size
    val remainingCount = 36 - totalCount
}
