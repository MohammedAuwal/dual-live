package com.duallive.app.ui.match

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.data.entity.Team

@Composable
fun MatchEntryScreen(
    teams: List<Team>,
    onLaunchDisplay: (Team, Team) -> Unit,
    onBack: () -> Unit
) {
    var homeTeam by remember { mutableStateOf<Team?>(null) }
    var awayTeam by remember { mutableStateOf<Team?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Match Setup", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Select opponents to start the live display", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.weight(1f)) {
            // Home Selection Column
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("HOME", fontWeight = FontWeight.Black, color = Color.Blue)
                Divider(Modifier.padding(vertical = 8.dp))
                LazyColumn {
                    items(teams) { team ->
                        TeamSelectCard(team, isSelected = homeTeam == team) { homeTeam = team }
                    }
                }
            }

            // VS Spacer
            Box(modifier = Modifier.fillMaxHeight().width(40.dp), contentAlignment = Alignment.Center) {
                Text("VS", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }

            // Away Selection Column
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("AWAY", fontWeight = FontWeight.Black, color = Color.Red)
                Divider(Modifier.padding(vertical = 8.dp))
                LazyColumn {
                    items(teams) { team ->
                        TeamSelectCard(team, isSelected = awayTeam == team) { awayTeam = team }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { if (homeTeam != null && awayTeam != null) onLaunchDisplay(homeTeam!!, awayTeam!!) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.medium,
            enabled = homeTeam != null && awayTeam != null && homeTeam != awayTeam
        ) {
            Text("GO LIVE (SCOREBOARD MODE)", fontWeight = FontWeight.Bold)
        }
        
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel")
        }
    }
}

@Composable
fun TeamSelectCard(team: Team, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 0.dp)
    ) {
        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
            Text(team.name, fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}
