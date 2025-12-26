package com.duallive.app.ui.match

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchEntryScreen(
    teams: List<Team>,
    onSaveMatch: (Int, Int, Int, Int) -> Unit,
    onBack: () -> Unit
) {
    var homeTeam by remember { mutableStateOf<Team?>(null) }
    var awayTeam by remember { mutableStateOf<Team?>(null) }
    var homeScore by remember { mutableStateOf("0") }
    var awayScore by remember { mutableStateOf("0") }
    var expandedHome by remember { mutableStateOf(false) }
    var expandedAway by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Record Match Result") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            
            // Home Team Selector
            Text("Home Team")
            Box {
                Button(onClick = { expandedHome = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(homeTeam?.name ?: "Select Home Team")
                }
                DropdownMenu(expanded = expandedHome, onDismissRequest = { expandedHome = false }) {
                    teams.forEach { team ->
                        DropdownMenuItem(text = { Text(team.name) }, onClick = { homeTeam = team; expandedHome = false })
                    }
                }
            }

            // Away Team Selector
            Text("Away Team")
            Box {
                Button(onClick = { expandedAway = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(awayTeam?.name ?: "Select Away Team")
                }
                DropdownMenu(expanded = expandedAway, onDismissRequest = { expandedAway = false }) {
                    teams.forEach { team ->
                        DropdownMenuItem(text = { Text(team.name) }, onClick = { awayTeam = team; expandedAway = false })
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = homeScore, onValueChange = { homeScore = it }, label = { Text("Home Score") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = awayScore, onValueChange = { awayScore = it }, label = { Text("Away Score") }, modifier = Modifier.weight(1f))
            }

            Button(
                onClick = { 
                    if (homeTeam != null && awayTeam != null && homeTeam != awayTeam) {
                        onSaveMatch(homeTeam!!.id, awayTeam!!.id, homeScore.toIntOrNull() ?: 0, awayScore.toIntOrNull() ?: 0)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = homeTeam != null && awayTeam != null && homeTeam != awayTeam
            ) {
                Text("Save Result & Update Table")
            }
            
            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
        }
    }
}
