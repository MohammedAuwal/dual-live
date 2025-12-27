package com.duallive.app.ui.league

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.Team
import com.duallive.app.data.entity.Standing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnockoutSelectionScreen(
    teams: List<Team>,
    standings: List<Standing>,
    onBack: () -> Unit,
    onConfirmKnockouts: (List<Team>, String) -> Unit
) {
    // 1. DYNAMIC SUGGESTION LOGIC
    val suggestedTeams = remember(standings, teams) {
        val grouped = teams.groupBy { it.groupName }
        val isComingFromGroups = grouped.keys.any { it != null }

        if (isComingFromGroups) {
            // STEP 1: Groups -> Pick top 2 from each group
            val winners = mutableListOf<Team>()
            grouped.forEach { (_, groupTeams) ->
                val ids = groupTeams.map { it.id }
                val topTwo = standings.filter { it.teamId in ids }
                    .sortedWith(compareByDescending<Standing> { it.points }.thenByDescending { it.goalsFor - it.goalsAgainst })
                    .take(2)
                winners.addAll(teams.filter { t -> topTwo.any { it.teamId == t.id } })
            }
            winners
        } else {
            // STEP 2: Knockout -> Pick only teams that won their match
            // In knockout mode, a team "won" if they have 1 played and 3 points (or more)
            teams.filter { t -> 
                val s = standings.find { it.teamId == t.id }
                (s?.wins ?: 0) > 0 
            }
        }
    }

    val selectedTeams = remember { mutableStateListOf<Team>().apply { addAll(suggestedTeams) } }
    
    // 2. SMART STAGE LABELING
    val stageName = when {
        selectedTeams.size > 4 && selectedTeams.size <= 8 -> "Quarter-Finals"
        selectedTeams.size > 2 && selectedTeams.size <= 4 -> "Semi-Finals"
        selectedTeams.size == 2 -> "Final"
        else -> "Knockout Round"
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("UCL Tournament Path") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🏆", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Next Stage: $stageName", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text("Confirm the winners to proceed", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(teams.sortedByDescending { t -> selectedTeams.contains(t) }) { team ->
                    val isSelected = selectedTeams.contains(team)
                    
                    ListItem(
                        headlineContent = { Text(team.name) },
                        supportingContent = { 
                            val s = standings.find { it.teamId == team.id }
                            Text("P: ${s?.matchesPlayed ?: 0} | W: ${s?.wins ?: 0} | GD: ${(s?.goalsFor ?: 0) - (s?.goalsAgainst ?: 0)}") 
                        },
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
                        }
                    )
                }
            }

            Button(
                onClick = { onConfirmKnockouts(selectedTeams.toList(), stageName) },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedTeams.size >= 2 && selectedTeams.size % 2 == 0
            ) {
                Text("Confirm $stageName Draw")
            }
            
            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Standings")
            }
        }
    }
}
