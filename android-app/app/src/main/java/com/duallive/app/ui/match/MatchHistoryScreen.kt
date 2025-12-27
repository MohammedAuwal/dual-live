package com.duallive.app.ui.match

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.Match
import com.duallive.app.data.entity.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchHistoryScreen(
    matches: List<Match>,
    teams: List<Team>,
    onDeleteMatch: (Match) -> Unit,
    onUpdateMatch: (Match) -> Unit,
    onBack: () -> Unit
) {
    var matchToDelete by remember { mutableStateOf<Match?>(null) }
    var matchToEdit by remember { mutableStateOf<Match?>(null) }
    
    val availableGroups = teams.mapNotNull { it.groupName }.distinct().sorted()
    var selectedGroup by remember { mutableStateOf<String?>(null) }

    val filteredMatches = if (selectedGroup == null) {
        matches
    } else {
        matches.filter { match ->
            val homeTeam = teams.find { it.id == match.homeTeamId }
            homeTeam?.groupName == selectedGroup
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Match Results", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        if (availableGroups.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedGroup == null,
                        onClick = { selectedGroup = null },
                        label = { Text("All") }
                    )
                }
                items(availableGroups) { group ->
                    FilterChip(
                        selected = selectedGroup == group,
                        onClick = { selectedGroup = group },
                        label = { Text(group) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredMatches.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text("No matches found", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredMatches.reversed()) { match ->
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
                                if (selectedGroup == null && availableGroups.isNotEmpty()) {
                                    val group = teams.find { it.id == match.homeTeamId }?.groupName
                                    if (group != null) {
                                        Text(group, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                            Row {
                                IconButton(onClick = { matchToEdit = match }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { matchToDelete = match }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Back") }

        // --- Edit Score Dialog ---
        if (matchToEdit != null) {
            var homeScoreText by remember { mutableStateOf(matchToEdit!!.homeScore.toString()) }
            var awayScoreText by remember { mutableStateOf(matchToEdit!!.awayScore.toString()) }

            AlertDialog(
                onDismissRequest = { matchToEdit = null },
                title = { Text("Edit Match Score") },
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = homeScoreText,
                            onValueChange = { homeScoreText = it },
                            modifier = Modifier.width(70.dp),
                            label = { Text("Home") }
                        )
                        Text("-", fontWeight = FontWeight.ExtraBold)
                        OutlinedTextField(
                            value = awayScoreText,
                            onValueChange = { awayScoreText = it },
                            modifier = Modifier.width(70.dp),
                            label = { Text("Away") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val h = homeScoreText.toIntOrNull() ?: 0
                        val a = awayScoreText.toIntOrNull() ?: 0
                        onUpdateMatch(matchToEdit!!.copy(homeScore = h, awayScore = a))
                        matchToEdit = null
                    }) { Text("UPDATE") }
                },
                dismissButton = {
                    TextButton(onClick = { matchToEdit = null }) { Text("CANCEL") }
                }
            )
        }

        if (matchToDelete != null) {
            AlertDialog(
                onDismissRequest = { matchToDelete = null },
                title = { Text("Delete Result?") },
                text = { Text("Remove this match? Points will be updated.") },
                confirmButton = {
                    TextButton(onClick = { 
                        onDeleteMatch(matchToDelete!!)
                        matchToDelete = null 
                    }) { Text("DELETE", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { matchToDelete = null }) { Text("CANCEL") }
                }
            )
        }
    }
}
