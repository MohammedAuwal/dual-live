package com.duallive.app.ui.table

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.data.entity.Standing
import com.duallive.app.data.entity.Team

@Composable
fun StandingsScreen(teams: List<Team>, standings: List<Standing>) {
    val groupedTeams = teams.groupBy { it.groupName }
    val isKnockoutStage = groupedTeams.keys.all { it == null } && teams.isNotEmpty()
    val isUclMode = groupedTeams.keys.any { it != null }

    val totalGoals = standings.sumOf { it.goalsFor }
    val totalMatches = standings.sumOf { it.matchesPlayed } / 2
    
    // Fixed: Comparison of Long IDs
    val topAttackTeamId = standings.maxByOrNull { it.goalsFor }?.teamId
    val topAttackName = teams.find { it.id == topAttackTeamId }?.name ?: "N/A"

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = if (isUclMode) "Tournament Standings" else if (isKnockoutStage) "Knockout Progress" else "League Table", 
            style = MaterialTheme.typography.headlineMedium, 
            fontWeight = FontWeight.Bold
        )
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Played", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text("$totalMatches", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
            Column {
                Text("Total Goals", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text("$totalGoals", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
            Column {
                Text("Best Attack", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text(topAttackName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }

        if (isKnockoutStage) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD4AF37)), 
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🏆", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("ROAD TO THE FINAL", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        Text("Finish all matches to proceed", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 4.dp))
        Text("Tie-breaker: GD > GF", style = MaterialTheme.typography.bodySmall)
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (isUclMode) {
                groupedTeams.forEach { (groupName, groupTeams) ->
                    item {
                        Text(
                            text = groupName ?: "Unassigned",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        val groupTeamIds = groupTeams.map { it.id }
                        // Fixed: Ensuring Long containment check
                        val groupStandings = standings.filter { it.teamId in groupTeamIds }
                        
                        StandingTable(teams = groupTeams, standings = groupStandings)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } else {
                item {
                    StandingTable(teams = teams, standings = standings)
                }
            }
        }
    }
}

@Composable
fun StandingTable(teams: List<Team>, standings: List<Standing>) {
    val scrollState = rememberScrollState()
    
    Column(modifier = Modifier.horizontalScroll(scrollState)) {
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
        
        standings.forEachIndexed { index, standing ->
            // Fixed: Comparison of Long teamId
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
