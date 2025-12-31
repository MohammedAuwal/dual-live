package com.duallive.app.ui.match

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.Match
import com.duallive.app.data.entity.Team
import com.duallive.app.utils.Fixture

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FixtureListScreen(
    fixtures: List<Fixture>,
    matches: List<Match>,
    onMatchSelect: (Team, Team) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Helper function to check if a fixture is played
    fun findMatchForFixture(fixture: Fixture): Match? {
        return matches.find { m ->
            m.homeTeamId == fixture.homeTeam.id && m.awayTeamId == fixture.awayTeam.id
        }
    }

    // Calculate Progress
    val totalFixtures = fixtures.size
    val completedCount = fixtures.count { findMatchForFixture(it) != null }
    val progressValue = if (totalFixtures > 0) completedCount.toFloat() / totalFixtures else 0f
    val percentage = (progressValue * 100).toInt()

    val filteredFixtures = remember(searchQuery, fixtures) {
        if (searchQuery.isBlank()) fixtures
        else fixtures.filter { 
            it.homeTeam.name.contains(searchQuery, ignoreCase = true) || 
            it.awayTeam.name.contains(searchQuery, ignoreCase = true) 
        }
    }

    val groupedFixtures = filteredFixtures.groupBy { it.round }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tournament Schedule", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = progressValue,
                modifier = Modifier.weight(1f).height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(
                text = "$percentage%",
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "$completedCount of $totalFixtures matches played",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            placeholder = { Text("Search team name...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            groupedFixtures.forEach { (round, matchesInRound) ->
                stickyHeader {
                    Text(
                        text = if (round > 0) "ROUND $round" else "KNOCKOUT STAGE",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                items(matchesInRound) { fixture ->
                    val completedMatch = findMatchForFixture(fixture)
                    val isDone = completedMatch != null

                    Card(
                        onClick = { if (!isDone) onMatchSelect(fixture.homeTeam, fixture.awayTeam) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDone) Color(0xFFE0E0E0).copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        enabled = !isDone
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fixture.homeTeam.name, 
                                modifier = Modifier.weight(1f),
                                color = if (isDone) Color.Gray else Color.Unspecified,
                                fontWeight = if(searchQuery.isNotEmpty() && fixture.homeTeam.name.contains(searchQuery, true)) FontWeight.ExtraBold else FontWeight.Normal
                            )
                            
                            if (isDone) {
                                Text(
                                    text = "${completedMatch?.homeScore} - ${completedMatch?.awayScore}",
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    fontWeight = FontWeight.Black,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Text("vs", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                            }

                            Text(
                                text = fixture.awayTeam.name, 
                                modifier = Modifier.weight(1f),
                                color = if (isDone) Color.Gray else Color.Unspecified,
                                fontWeight = if(searchQuery.isNotEmpty() && fixture.awayTeam.name.contains(searchQuery, true)) FontWeight.ExtraBold else FontWeight.Normal,
                                textAlign = androidx.compose.ui.text.style.TextAlign.End
                            )
                            
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.padding(start = 8.dp).size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Back to Dashboard")
        }
    }
}
