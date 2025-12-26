package com.duallive.app.ui.match

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.Team
import com.duallive.app.utils.Fixture

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FixtureListScreen(
    fixtures: List<Fixture>,
    onMatchSelect: (Team, Team) -> Unit,
    onBack: () -> Unit
) {
    val groupedFixtures = fixtures.groupBy { it.round }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tournament Schedule", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
            groupedFixtures.forEach { (round, matchesInRound) ->
                stickyHeader {
                    Text(
                        text = "ROUND $round",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                items(matchesInRound) { fixture ->
                    Card(
                        onClick = { onMatchSelect(fixture.homeTeam, fixture.awayTeam) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(fixture.homeTeam.name, modifier = Modifier.weight(1f))
                            Text("vs", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                            Text(fixture.awayTeam.name, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Dashboard")
        }
    }
}
