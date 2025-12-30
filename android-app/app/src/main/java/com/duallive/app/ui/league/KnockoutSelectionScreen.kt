package com.duallive.app.ui.league

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onConfirmKnockouts: (List<Team>, String) -> Unit
) {
    // 1. DYNAMIC SUGGESTION LOGIC
    val suggestedTeams = remember(standings, teams) {
        val grouped = teams.groupBy { it.groupName }
        val isComingFromGroups = grouped.keys.any { it != null }

        if (isComingFromGroups) {
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
            // Knockout Logic: Pick teams that have a Win in this stage
            teams.filter { t -> 
                val s = standings.find { it.teamId == t.id }
                (s?.wins ?: 0) > 0 
            }
        }
    }

    val selectedTeams = remember { mutableStateListOf<Team>().apply { addAll(suggestedTeams) } }
    
    // 2. STAGE LABELING FIX
    val nextStageName = when (teams.size) {
        16 -> "Quarter-Finals"
        8 -> "Semi-Finals"
        4 -> "Final"
        2 -> "Champion"
        else -> "Next Round"
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
                        Text("Current Stage: ${if(teams.any { it.groupName != null }) "Groups" else "Knockout"}", style = MaterialTheme.typography.bodySmall)
                        Text("Proceed to: $nextStageName", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(teams.sortedByDescending { t -> selectedTeams.contains(t) }) { team ->
                    val isSelected = selectedTeams.contains(team)
                    val s = standings.find { it.teamId == team.id }
                    
                    ListItem(
                        headlineContent = { Text(team.name) },
                        supportingContent = { 
                            Text("Wins: ${s?.wins ?: 0} | GD: ${(s?.goalsFor ?: 0) - (s?.goalsAgainst ?: 0)}") 
                        },
                        trailingContent = {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    if (checked) { if (!selectedTeams.contains(team)) selectedTeams.add(team) }
                                    else { selectedTeams.remove(team) }
                                }
                            )
                        },
                        modifier = Modifier.clickable {
                            if (isSelected) selectedTeams.remove(team) 
                            else if (!selectedTeams.contains(team)) selectedTeams.add(team)
                        }
                    )
                }
            }

            Button(
                onClick = { onConfirmKnockouts(selectedTeams.toList(), nextStageName) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = selectedTeams.size > 0 && (selectedTeams.size % 2 == 0 || nextStageName == "Champion")
            ) {
                Text(if (nextStageName == "Champion") "DECLARE CHAMPION" else "CREATE $nextStageName DRAW")
            }
            
            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Standings")
            }
        }
    }
}
