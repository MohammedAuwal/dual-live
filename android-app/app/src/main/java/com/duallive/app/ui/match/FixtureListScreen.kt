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
    teams: List<Team>,
    onMatchSelect: (Team, Team) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val teamNames = remember(teams) { teams.associate { it.id to it.name } }

    // This is the specific fix for the Classic League Green Tick
    fun findMatchForFixture(fixture: Fixture): Match? {
        return matches.find { m ->
            val hMatch = (m.homeTeamId == fixture.homeTeam.id && m.awayTeamId == fixture.awayTeam.id)
            val hNameMatch = (teamNames[m.homeTeamId] == fixture.homeTeam.name && teamNames[m.awayTeamId] == fixture.awayTeam.name)
            hMatch || hNameMatch
        }
    }

    val filteredFixtures = fixtures.filter { 
        it.homeTeam.name.contains(searchQuery, true) || it.awayTeam.name.contains(searchQuery, true) 
    }

    val groupedFixtures = filteredFixtures.groupBy { it.round }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Fixtures", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            placeholder = { Text("Search teams...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            groupedFixtures.forEach { (round, roundMatches) ->
                stickyHeader {
                    Text(
                        text = if (round > 0) "ROUND $round" else "MATCHES",
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer).padding(8.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                items(roundMatches) { fixture ->
                    val matchData = findMatchForFixture(fixture)
                    val isDone = matchData != null

                    Card(
                        onClick = { if (!isDone) onMatchSelect(fixture.homeTeam, fixture.awayTeam) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDone) Color.LightGray.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        enabled = !isDone
                    ) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(fixture.homeTeam.name, modifier = Modifier.weight(1f))
                            if (isDone) {
                                Text("${matchData?.homeScore} - ${matchData?.awayScore}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                            } else {
                                Text("vs", modifier = Modifier.padding(horizontal = 8.dp))
                            }
                            Text(fixture.awayTeam.name, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                        }
                    }
                }
            }
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Back to Dashboard") }
    }
}
