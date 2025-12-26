package com.duallive.app.ui.table

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.Standing
import com.duallive.app.data.entity.Team

@Composable
fun StandingsScreen(teams: List<Team>, standings: List<Standing>) {
    val totalTeams = teams.size
    val matchesPerTeam = if (totalTeams > 0) totalTeams - 1 else 0

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("League Table", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Each team plays $matchesPerTeam matches total", style = MaterialTheme.typography.bodySmall)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text("Team", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
            Text("P/Rem", modifier = Modifier.width(60.dp), fontWeight = FontWeight.Bold) // Played / Remaining
            Text("Pts", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold)
        }
        Divider(modifier = Modifier.padding(vertical = 4.dp))
        
        LazyColumn {
            items(standings) { standing ->
                val teamName = teams.find { it.id == standing.teamId }?.name ?: "Unknown"
                val remaining = matchesPerTeam - standing.matchesPlayed
                
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text(teamName, modifier = Modifier.weight(1f))
                    // Shows "5/14" meaning 5 played, 14 remaining
                    Text("${standing.matchesPlayed}/$remaining", modifier = Modifier.width(60.dp))
                    Text("${standing.points}", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
