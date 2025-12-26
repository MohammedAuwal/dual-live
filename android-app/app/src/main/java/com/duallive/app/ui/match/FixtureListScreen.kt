package com.duallive.app.ui.match

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.Team
import com.duallive.app.utils.Fixture

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixtureListScreen(
    fixtures: List<Fixture>,
    onMatchSelect: (Team, Team) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Automatic Fixtures", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Tap a match to launch the Scoreboard", style = MaterialTheme.typography.bodySmall)
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(fixtures) { fixture ->
                Card(
                    onClick = { onMatchSelect(fixture.homeTeam, fixture.awayTeam) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(fixture.homeTeam.name, modifier = Modifier.weight(1f))
                        Text("VS", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(fixture.awayTeam.name, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                    }
                }
            }
        }
        
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text("Back")
        }
    }
}
