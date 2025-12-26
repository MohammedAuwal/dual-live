package com.duallive.app.ui.match

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.Match
import com.duallive.app.data.entity.Team

@Composable
fun MatchHistoryScreen(
    matches: List<Match>,
    teams: List<Team>,
    onDeleteMatch: (Match) -> Unit,
    onBack: () -> Unit
) {
    var matchToDelete by remember { mutableStateOf<Match?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Match Results", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))

        if (matches.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No matches played yet", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(matches.reversed()) { match ->
                    val home = teams.find { it.id == match.homeTeamId }?.name ?: "Unknown"
                    val away = teams.find { it.id == match.awayTeamId }?.name ?: "Unknown"

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("$home vs $away", fontWeight = FontWeight.Bold)
                                Text("Score: ${match.homeScore} - ${match.awayScore}", style = MaterialTheme.typography.bodyMedium)
                            }
                            IconButton(onClick = { matchToDelete = match }) {
                                Icon(
                                    imageVector = Icons.Default.Delete as ImageVector,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Back") }

        if (matchToDelete != null) {
            AlertDialog(
                onDismissRequest = { matchToDelete = null },
                title = { Text("Delete Result?") },
                text = { Text("Remove this match? Points will be updated.") },
                confirmButton = {
                    TextButton(onClick = { 
                        onDeleteMatch(matchToDelete!!)
                        matchToDelete = null 
                    }) { Text("DELETE", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { matchToDelete = null }) { Text("CANCEL") }
                }
            )
        }
    }
}
