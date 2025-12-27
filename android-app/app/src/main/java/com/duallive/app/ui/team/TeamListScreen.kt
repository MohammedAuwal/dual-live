package com.duallive.app.ui.team

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamListScreen(
    leagueName: String,
    teams: List<Team>,
    isUcl: Boolean,
    onBack: () -> Unit,
    onAddTeamClick: () -> Unit,
    onUpdateTeam: (Team) -> Unit
) {
    var teamToEdit by remember { mutableStateOf<Team?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(leagueName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                text = "${teams.size} Teams Registered",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            if (teams.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Button(onClick = onAddTeamClick) {
                        Icon(Icons.Default.Person, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add Your First Teams")
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(teams) { team ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth().height(100.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = team.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                    IconButton(
                                        onClick = { teamToEdit = team },
                                        modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                if (team.groupName != null) {
                                    Text(
                                        text = team.groupName!!,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                    item {
                        OutlinedCard(
                            onClick = onAddTeamClick,
                            modifier = Modifier.fillMaxWidth().height(100.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("+ Add Teams", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Edit Dialog ---
    if (teamToEdit != null) {
        var editName by remember { mutableStateOf(teamToEdit!!.name) }
        var editGroup by remember { mutableStateOf(teamToEdit!!.groupName ?: "") }

        AlertDialog(
            onDismissRequest = { teamToEdit = null },
            title = { Text("Edit Team") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Team Name") }
                    )
                    if (isUcl) {
                        OutlinedTextField(
                            value = editGroup,
                            onValueChange = { editGroup = it },
                            label = { Text("Group (e.g. Group A)") }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdateTeam(teamToEdit!!.copy(name = editName, groupName = editGroup.ifBlank { null }))
                    teamToEdit = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { teamToEdit = null }) { Text("Cancel") }
            }
        )
    }
}
