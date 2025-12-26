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
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("League Table", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text("Team", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
            Text("P", modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold)
            Text("W", modifier = Modifier.width(30.dp))
            Text("D", modifier = Modifier.width(30.dp))
            Text("L", modifier = Modifier.width(30.dp))
            Text("Pts", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold)
        }
        Divider(modifier = Modifier.padding(vertical = 4.dp))
        
        LazyColumn {
            items(standings) { standing ->
                val teamName = teams.find { it.id == standing.teamId }?.name ?: "Unknown"
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text(teamName, modifier = Modifier.weight(1f))
                    Text("${standing.matchesPlayed}", modifier = Modifier.width(30.dp))
                    Text("${standing.wins}", modifier = Modifier.width(30.dp))
                    Text("${standing.draws}", modifier = Modifier.width(30.dp))
                    Text("${standing.losses}", modifier = Modifier.width(30.dp))
                    Text("${standing.points}", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
