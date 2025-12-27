package com.duallive.app.ui.league

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.Team
import com.duallive.app.data.entity.Standing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnockoutSelectionScreen(
    teams: List<Team>,
    standings: List<Standing>,
    onBack: () -> Unit,
    onConfirmKnockouts: (List<Team>) -> Unit
) {
    // Logic: Auto-select Top 2 from each group
    val suggestedTeams = remember(standings) {
        val winners = mutableListOf<Team>()
        val groupedStandings = standings.groupBy { teamId -> 
            teams.find { it.id == teamId.teamId }?.groupName 
        }
        
        groupedStandings.forEach { (_, groupStandings) ->
            // Standings are already sorted by Points -> GD -> GF in TableCalculator
            val topTwoIds = groupStandings.take(2).map { it.teamId }
            winners.addAll(teams.filter { it.id in topTwoIds })
        }
        winners
    }

    val selectedTeams = remember { mutableStateListOf<Team>().apply { addAll(suggestedTeams) } }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Knockout Qualifiers") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Stars, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Top 2 from each group are auto-selected. Adjust if needed.", style = MaterialTheme.typography.bodySmall)
                }
            }
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(teams.sortedBy { it.groupName }) { team ->
                    val isSelected = selectedTeams.contains(team)
                    val isAutoSuggested = suggestedTeams.contains(team)
                    
                    ListItem(
                        headlineContent = { 
                            Text(team.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) 
                        },
                        supportingContent = { Text(team.groupName ?: "No Group") },
                        trailingContent = {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    if (checked) selectedTeams.add(team) else selectedTeams.remove(team)
                                }
                            )
                        },
                        modifier = Modifier.clickable {
                            if (isSelected) selectedTeams.remove(team) else selectedTeams.add(team)
                        },
                        colors = if (isAutoSuggested && isSelected) 
                            ListItemDefaults.colors(containerColor = Color.Green.copy(alpha = 0.05f))
                            else ListItemDefaults.colors()
                    )
                }
            }

            Button(
                onClick = { onConfirmKnockouts(selectedTeams.toList()) },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedTeams.isNotEmpty()
            ) {
                Text("Create Knockout Phase (${selectedTeams.size} Teams)")
            }
            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel")
            }
        }
    }
}
