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
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.League

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
        topBar = { TopAppBar(title = { Text("My Leagues") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddLeagueClick) {
                Icon(Icons.Default.Add, contentDescription = "Add League")
            }
        }
    ) { padding ->
        if (leagues.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No leagues created yet. Tap + to start.")
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
                                Text(text = league.name, style = MaterialTheme.typography.titleLarge)
                                league.description?.let {
                                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            IconButton(onClick = { leagueToDelete = league }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete League",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog
    if (leagueToDelete != null) {
        AlertDialog(
            onDismissRequest = { leagueToDelete = null },
            title = { Text("Delete League?") },
            text = { Text("This will permanently delete '${leagueToDelete?.name}' and all its data.") },
            confirmButton = {
                TextButton(onClick = { 
                    onDeleteLeague(leagueToDelete!!)
                    leagueToDelete = null 
                }) { Text("DELETE", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { leagueToDelete = null }) { Text("CANCEL") }
            }
        )
    }
}
