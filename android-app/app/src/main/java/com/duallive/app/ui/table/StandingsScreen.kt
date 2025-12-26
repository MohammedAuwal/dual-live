package com.duallive.app.ui.table

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("League Table", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Tie-breaker: GD > GF", style = MaterialTheme.typography.bodySmall)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val scrollState = rememberScrollState()
        
        Column(modifier = Modifier.horizontalScroll(scrollState)) {
            // Header Row
            Row(modifier = Modifier.padding(8.dp)) {
                Text("#", modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold)
                Text("Team", modifier = Modifier.width(100.dp), fontWeight = FontWeight.Bold)
                Text("P", modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold)
                Text("W", modifier = Modifier.width(30.dp))
                Text("D", modifier = Modifier.width(30.dp))
                Text("L", modifier = Modifier.width(30.dp))
                Text("GF", modifier = Modifier.width(35.dp))
                Text("GA", modifier = Modifier.width(35.dp))
                Text("GD", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold)
                Text("Pts", modifier = Modifier.width(45.dp), fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            }
            Divider()
            
            LazyColumn {
                itemsIndexed(standings) { index, standing ->
                    val teamName = teams.find { it.id == standing.teamId }?.name ?: "Unknown"
                    val gd = standing.goalsFor - standing.goalsAgainst
                    
                    Row(modifier = Modifier.padding(8.dp)) {
                        Text("${index + 1}", modifier = Modifier.width(30.dp))
                        Text(teamName, modifier = Modifier.width(100.dp), maxLines = 1)
                        Text("${standing.matchesPlayed}", modifier = Modifier.width(30.dp))
                        Text("${standing.wins}", modifier = Modifier.width(30.dp))
                        Text("${standing.draws}", modifier = Modifier.width(30.dp))
                        Text("${standing.losses}", modifier = Modifier.width(30.dp))
                        Text("${standing.goalsFor}", modifier = Modifier.width(35.dp))
                        Text("${standing.goalsAgainst}", modifier = Modifier.width(35.dp))
                        Text("${if (gd > 0) "+" else ""}$gd", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold)
                        Text("${standing.points}", modifier = Modifier.width(45.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Divider(thickness = 0.5.dp)
                }
            }
        }
    }
}
