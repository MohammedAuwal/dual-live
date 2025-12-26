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
    onLaunchDisplay: (Team, Team) -> Unit,
    onBack: () -> Unit
) {
    var homeTeam by remember { mutableStateOf<Team?>(null) }
    var awayTeam by remember { mutableStateOf<Team?>(null) }
    var homeExpanded by remember { mutableStateOf(false) }
    var awayExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Match Setup") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            
            Text("Select Teams for Live Display", style = MaterialTheme.typography.titleMedium)
            
            // Home Team Selector
            Box {
                Button(onClick = { homeExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(homeTeam?.name ?: "Select Home Team")
                }
                DropdownMenu(expanded = homeExpanded, onDismissRequest = { homeExpanded = false }) {
                    teams.forEach { team ->
                        DropdownMenuItem(text = { Text(team.name) }, onClick = { homeTeam = team; homeExpanded = false })
                    }
                }
            }

            // Away Team Selector
            Box {
                Button(onClick = { awayExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(awayTeam?.name ?: "Select Away Team")
                }
                DropdownMenu(expanded = awayExpanded, onDismissRequest = { awayExpanded = false }) {
                    teams.filter { it.id != homeTeam?.id }.forEach { team ->
                        DropdownMenuItem(text = { Text(team.name) }, onClick = { awayTeam = team; awayExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { if (homeTeam != null && awayTeam != null) onLaunchDisplay(homeTeam!!, awayTeam!!) },
                modifier = Modifier.fillMaxWidth(),
                enabled = homeTeam != null && awayTeam != null && homeTeam != awayTeam
            ) {
                Text("LAUNCH SCOREBOARD (TV MODE)")
            }
            
            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
        }
    }
}
