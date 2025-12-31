package com.duallive.app.ui.table

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import com.duallive.app.ui.components.GlassCard

@Composable
fun StandingsScreen(teams: List<Team>, standings: List<Standing>) {
    val groupedTeams = teams.groupBy { it.groupName }
    val isKnockoutStage = groupedTeams.keys.all { it == null } && teams.isNotEmpty()
    val isUclMode = groupedTeams.keys.any { it != null }
    val accentColor = if (isUclMode || isKnockoutStage) Color(0xFFE3BC63) else Color.White

    val totalGoals = standings.sumOf { it.goalsFor }
    val totalMatches = if (standings.isNotEmpty()) standings.sumOf { it.matchesPlayed } / 2 else 0
    val topAttackTeamId = standings.maxByOrNull { it.goalsFor }?.teamId
    val topAttackName = teams.find { it.id == topAttackTeamId }?.name ?: "N/A"

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = if (isUclMode) "Tournament Standings" else if (isKnockoutStage) "Knockout Progress" else "League Table", 
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem("Played", "$totalMatches", accentColor)
            StatItem("Total Goals", "$totalGoals", accentColor)
            StatItem("Best Attack", topAttackName, accentColor)
        }

        if (isKnockoutStage) {
            GlassCard(tintColor = Color(0xFFE3BC63), modifier = Modifier.padding(bottom = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏆", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("ROAD TO THE FINAL", color = Color(0xFFE3BC63), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        Text("Finish all matches to proceed", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }
            }
        }

        Text(
            text = "Tie-breaker: GD > GF", 
            style = MaterialTheme.typography.bodySmall, 
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (isUclMode) {
                groupedTeams.forEach { (groupName, groupTeams) ->
                    item {
                        Text(
                            text = groupName ?: "Unassigned",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                        val groupTeamIds = groupTeams.map { it.id }
                        val groupStandings = standings.filter { it.teamId in groupTeamIds }
                        
                        GlassCard(tintColor = accentColor) {
                            StandingTable(teams = groupTeams, standings = groupStandings, accentColor = accentColor)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } else {
                item {
                    GlassCard(tintColor = accentColor) {
                        StandingTable(teams = teams, standings = standings, accentColor = accentColor)
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, accent: Color) {
    Column {
        Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = accent)
    }
}

@Composable
fun StandingTable(teams: List<Team>, standings: List<Standing>, accentColor: Color) {
    val scrollState = rememberScrollState()
    
    Column(modifier = Modifier.horizontalScroll(scrollState)) {
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            HeaderText("#", 35.dp)
            HeaderText("Team", 120.dp)
            HeaderText("P", 35.dp)
            HeaderText("W", 35.dp)
            HeaderText("D", 35.dp)
            HeaderText("L", 35.dp)
            HeaderText("GD", 45.dp)
            HeaderText("Pts", 50.dp, accentColor)
        }
        
        Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
        
        standings.forEachIndexed { index, standing ->
            val teamName = teams.find { it.id == standing.teamId }?.name ?: "Unknown"
            val gd = standing.goalsFor - standing.goalsAgainst
            
            Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("${index + 1}", modifier = Modifier.width(35.dp), color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                Text(teamName, modifier = Modifier.width(120.dp), color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("${standing.matchesPlayed}", modifier = Modifier.width(35.dp), color = Color.White, fontSize = 13.sp)
                Text("${standing.wins}", modifier = Modifier.width(35.dp), color = Color.White, fontSize = 13.sp)
                Text("${standing.draws}", modifier = Modifier.width(35.dp), color = Color.White, fontSize = 13.sp)
                Text("${standing.losses}", modifier = Modifier.width(35.dp), color = Color.White, fontSize = 13.sp)
                Text("${if (gd > 0) "+" else ""}$gd", modifier = Modifier.width(45.dp), color = if(gd >= 0) Color.White else Color(0xFFCF6679), fontWeight = FontWeight.Medium)
                Text("${standing.points}", modifier = Modifier.width(50.dp), fontWeight = FontWeight.Black, color = accentColor, fontSize = 15.sp)
            }
            if (index < standings.size - 1) {
                Divider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)
            }
        }
    }
}

@Composable
fun HeaderText(text: String, width: androidx.compose.ui.unit.Dp, color: Color = Color.White) {
    Text(
        text = text, 
        modifier = Modifier.width(width), 
        fontWeight = FontWeight.Bold, 
        fontSize = 12.sp, 
        color = color.copy(alpha = 0.8f)
    )
}
