package com.duallive.app.ui.table

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
    val totalMatchesPerTeam = if (totalTeams > 0) totalTeams - 1 else 0

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("League Table", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Format: Round Robin", style = MaterialTheme.typography.bodySmall)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val scrollState = rememberScrollState()
        
        Column(modifier = Modifier.horizontalScroll(scrollState)) {
            Row(modifier = Modifier.padding(8.dp)) {
                Text("Team", modifier = Modifier.width(120.dp), fontWeight = FontWeight.Bold)
                Text("P", modifier = Modifier.width(35.dp), fontWeight = FontWeight.Bold)
                Text("W", modifier = Modifier.width(35.dp))
                Text("D", modifier = Modifier.width(35.dp))
                Text("L", modifier = Modifier.width(35.dp))
                Text("Rem", modifier = Modifier.width(45.dp), color = MaterialTheme.colorScheme.secondary)
                Text("Pts", modifier = Modifier.width(45.dp), fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            }
            Divider()
            
            LazyColumn {
                items(standings) { standing ->
                    val teamName = teams.find { it.id == standing.teamId }?.name ?: "Unknown"
                    val remaining = totalMatchesPerTeam - standing.matchesPlayed
                    
                    Row(modifier = Modifier.padding(8.dp)) {
                        Text(teamName, modifier = Modifier.width(120.dp), maxLines = 1)
                        Text("${standing.matchesPlayed}", modifier = Modifier.width(35.dp))
                        Text("${standing.wins}", modifier = Modifier.width(35.dp))
                        Text("${standing.draws}", modifier = Modifier.width(35.dp))
                        Text("${standing.losses}", modifier = Modifier.width(35.dp))
                        Text("$remaining", modifier = Modifier.width(45.dp), color = MaterialTheme.colorScheme.secondary)
                        Text("${standing.points}", modifier = Modifier.width(45.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Divider(thickness = 0.5.dp)
                }
            }
        }
    }
}
