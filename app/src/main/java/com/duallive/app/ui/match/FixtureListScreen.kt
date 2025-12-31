package com.duallive.app.ui.match

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.sp
import com.duallive.app.data.entity.Match
import com.duallive.app.data.entity.Team
import com.duallive.app.ui.components.GlassCard
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
    
    val isUcl = fixtures.any { it.round == 0 }
    val accentColor = if (isUcl) Color(0xFFE3BC63) else Color.White

    fun findMatchForFixture(fixture: Fixture): Match? {
        return matches.find { m ->
            m.homeTeamId == fixture.homeTeam.id && m.awayTeamId == fixture.awayTeam.id
        }
    }

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
        Text("Tournament Schedule", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            // FIXED: Passing Float directly, not a lambda
            LinearProgressIndicator(
                progress = progressValue,
                modifier = Modifier.weight(1f).height(8.dp),
                color = accentColor,
                trackColor = Color.White.copy(alpha = 0.1f),
            )
            Text(text = "$percentage%", modifier = Modifier.padding(start = 12.dp), color = accentColor, fontWeight = FontWeight.Bold)
        }
        
        Text(text = "$completedCount of $totalFixtures matches played", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            placeholder = { Text("Search team...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = accentColor) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
        
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            groupedFixtures.forEach { (round, matchesInRound) ->
                stickyHeader {
                    Surface(color = Color(0xFF0A192F).copy(alpha = 0.9f), modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (round > 0) "ROUND $round" else "KNOCKOUT STAGE",
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                items(matchesInRound) { fixture ->
                    val completedMatch = findMatchForFixture(fixture)
                    val isDone = completedMatch != null

                    // FIXED: Moved onClick to Modifier.clickable if GlassCard doesn't have internal onClick
                    GlassCard(
                        modifier = Modifier.fillMaxWidth().clickable(enabled = !isDone) { 
                            onMatchSelect(fixture.homeTeam, fixture.awayTeam) 
                        },
                        tintColor = if (isDone) Color.Transparent else accentColor
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = fixture.homeTeam.name, modifier = Modifier.weight(1f), color = if (isDone) Color.White.copy(alpha = 0.5f) else Color.White)
                            
                            if (isDone) {
                                Text("${completedMatch?.homeScore} - ${completedMatch?.awayScore}", fontWeight = FontWeight.ExtraBold, color = accentColor)
                            } else {
                                Text("VS", color = accentColor.copy(alpha = 0.5f), fontSize = 12.sp)
                            }

                            Text(text = fixture.awayTeam.name, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End, color = if (isDone) Color.White.copy(alpha = 0.5f) else Color.White)
                        }
                    }
                }
            }
        }

        Button(
            onClick = onBack, 
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
        ) {
            Text("BACK TO DASHBOARD", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
