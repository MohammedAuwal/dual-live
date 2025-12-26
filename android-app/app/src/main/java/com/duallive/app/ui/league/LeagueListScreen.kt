package com.duallive.app.ui.league

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.League

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueListScreen(
    leagues: List<League>,
    onLeagueClick: (League) -> Unit,
    onAddLeagueClick: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("My Leagues") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddLeagueClick) {
                Icon(Icons.Default.Add, contentDescription = "Add League")
            }
        }
    ) { padding ->
        if (leagues.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
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
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = league.name, style = MaterialTheme.typography.titleLarge)
                            league.description?.let {
                                Text(text = it, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
