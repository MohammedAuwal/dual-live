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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueListScreen(
    leagues: List<League>,
    onLeagueClick: (League) -> Unit,
    onAddLeagueClick: () -> Unit,
    onDeleteLeague: (League) -> Unit
) {
    var leagueToDelete by remember { mutableStateOf<League?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Leagues", fontWeight = FontWeight.Bold) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddLeagueClick) {
                Icon(Icons.Default.Add, contentDescription = "Add League")
            }
        }
    ) { padding ->
        if (leagues.isEmpty()) {
            // Enhanced Empty State based on your request
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "You did not create or join any league yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onAddLeagueClick) {
                        Text("Start Now")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(leagues) { league ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLeagueClick(league) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = league.name, style = MaterialTheme.typography.titleLarge)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    SuggestionChip(
                                        onClick = { },
                                        label = { 
                                            Text(
                                                text = if (league.type == LeagueType.UCL) "UCL" else "Classic",
                                                style = MaterialTheme.typography.labelSmall
                                            ) 
                                        },
                                        enabled = false
                                    )
                                }
                                league.description?.let {
                                    Text(text = it, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                }
                            }
                            IconButton(onClick = { leagueToDelete = league }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete League",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (leagueToDelete != null) {
        AlertDialog(
            onDismissRequest = { leagueToDelete = null },
            title = { Text("Delete League?") },
            text = { Text("This will permanently delete '${leagueToDelete?.name}' and all its data.") },
            confirmButton = {
                TextButton(onClick = { 
                    onDeleteLeague(leagueToDelete!!)
                    leagueToDelete = null 
                }) { Text("DELETE", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { leagueToDelete = null }) { Text("CANCEL") }
            }
        )
    }
}
