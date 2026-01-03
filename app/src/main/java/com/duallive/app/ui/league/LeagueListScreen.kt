package com.duallive.app.ui.league

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.data.entity.League
import com.duallive.app.data.entity.LeagueType
import com.duallive.app.ui.components.GlassCard

@Composable
fun LeagueListScreen(
    leagues: List<League>,
    type: LeagueType,
    onLeagueClick: (League) -> Unit,
    onAddLeagueClick: () -> Unit,
    onDeleteLeague: (League) -> Unit
) {
    var leagueToDelete by remember { mutableStateOf<League?>(null) }
    
    // THEME LOGIC: Now handling all 3 types correctly to prevent "dead" buttons
    val accentColor = when (type) {
        LeagueType.UCL -> Color(0xFFE3BC63)   // Gold for Old UCL
        LeagueType.SWISS -> Color(0xFF00BFFF) // Blue for New Swiss
        else -> Color.White                   // White for Classic
    }

    val screenTitle = when (type) {
        LeagueType.UCL -> "UCL Tournaments"
        LeagueType.SWISS -> "New UCL Swiss"
        else -> "Classic Leagues"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = screenTitle,
                    color = accentColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                
                // Fixed: Explicitly calling the lambda
                IconButton(onClick = { onAddLeagueClick() }) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = accentColor)
                }
            }

            if (leagues.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No $screenTitle found.\nTap + to start.",
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(leagues) { league ->
                        GlassCard(
                            tintColor = accentColor,
                            modifier = Modifier.clickable { onLeagueClick(league) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = league.name,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Code: ${league.inviteCode}",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                }
                                
                                IconButton(onClick = { leagueToDelete = league }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation
    if (leagueToDelete != null) {
        AlertDialog(
            onDismissRequest = { leagueToDelete = null },
            containerColor = Color(0xFF0A192F),
            title = { Text("Delete Tournament?", color = Color.White) },
            text = { Text("This will remove all stats for ${leagueToDelete?.name}.", color = Color.White.copy(alpha = 0.7f)) },
            confirmButton = {
                TextButton(onClick = { 
                    onDeleteLeague(leagueToDelete!!)
                    leagueToDelete = null 
                }) { Text("DELETE", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { leagueToDelete = null }) { Text("CANCEL", color = Color.White) }
            }
        )
    }
}
