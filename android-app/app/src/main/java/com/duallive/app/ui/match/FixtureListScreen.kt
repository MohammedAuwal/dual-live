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
import androidx.compose.ui.text.style.TextAlign
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

    val teamIdToName = remember(teams) {
        teams.associate { it.id to it.name }
    }

    fun findMatchForFixture(fixture: Fixture): Match? {
        return matches.find { m ->
            val homeName = teamIdToName[m.homeTeamId]
            val awayName = teamIdToName[m.awayTeamId]
            homeName == fixture.homeTeam.name &&
            awayName == fixture.awayTeam.name
        }
    }

    val totalFixtures = fixtures.size
    val completedCount = fixtures.count { findMatchForFixture(it) != null }
    val progressValue =
        if (totalFixtures > 0) completedCount.toFloat() / totalFixtures else 0f
    val percentage = (progressValue * 100).toInt()

    val filteredFixtures = remember(searchQuery, fixtures) {
        if (searchQuery.isBlank()) fixtures
        else fixtures.filter {
            it.homeTeam.name.contains(searchQuery, ignoreCase = true) ||
            it.awayTeam.name.contains(searchQuery, ignoreCase = true)
        }
    }

    val groupedFixtures = filteredFixtures.groupBy { it.round }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tournament Schedule",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = progressValue,
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(
                text = "$percentage%",
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            placeholder = { Text("Search team name...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            singleLine = true
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            groupedFixtures.forEach { (round, roundFixtures) ->
                stickyHeader {
                    Text(
                        text = if (round > 0) "ROUND $round" else "MATCHES",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(8.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                items(roundFixtures) { fixture ->
                    val matchData = findMatchForFixture(fixture)
                    val isDone = matchData != null

                    Card(
                        onClick = {
                            if (!isDone) {
                                onMatchSelect(fixture.homeTeam, fixture.awayTeam)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        enabled = !isDone,
                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (isDone)
                                    Color.LightGray.copy(alpha = 0.4f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fixture.homeTeam.name,
                                modifier = Modifier.weight(1f)
                            )

                            if (isDone) {
                                Text(
                                    text = "${matchData?.homeScore} - ${matchData?.awayScore}",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50)
                                )
                            } else {
                                Text(
                                    text = "vs",
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }

                            Text(
                                text = fixture.awayTeam.name,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Back to Dashboard")
        }
    }
}
