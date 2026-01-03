package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.ucl2026.viewmodel.Ucl26ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ucl26MatchScreen(viewModel: Ucl26ViewModel, onBack: () -> Unit) {
    val matches by viewModel.matches.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val currentRound by viewModel.currentRound.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF00122E))) {
        TopAppBar(
            title = { Text("Round $currentRound", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
            },
            actions = {
                // The Reset Button
                IconButton(onClick = { showResetDialog = true }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White.copy(alpha = 0.7f))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(matches) { match ->
                val homeTeam = teams.find { it.id == match.homeTeamId }
                val awayTeam = teams.find { it.id == match.awayTeamId }

                MatchResultCard(
                    homeName = homeTeam?.name ?: "Team ${match.homeTeamId}", 
                    awayName = awayTeam?.name ?: "Team ${match.awayTeamId}",
                    homeScore = match.homeScore,
                    awayScore = match.awayScore,
                    onScoreChange = { h, a -> 
                        viewModel.updateScore(match.matchId, h, a)
                    }
                )
            }
        }
        
        Button(
            onClick = { viewModel.nextRound() },
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("FINALIZE ROUND & RE-RANK", color = Color(0xFF00122E), fontWeight = FontWeight.Bold)
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = Color(0xFF0A192F),
            title = { Text("Reset Scores?", color = Color.White) },
            text = { Text("This will set all scores in Round $currentRound back to 0-0.", color = Color.White.copy(alpha = 0.7f)) },
            confirmButton = {
                TextButton(onClick = { 
                    matches.forEach { viewModel.updateScore(it.matchId, 0, 0) }
                    showResetDialog = false 
                }) { Text("RESET", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("CANCEL", color = Color.White) }
            }
        )
    }
}

@Composable
fun MatchResultCard(homeName: String, awayName: String, homeScore: Int, awayScore: Int, onScoreChange: (Int, Int) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x1AFFFFFF)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Team
            Column(modifier = Modifier.weight(1f)) {
                Text(homeName, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                ScoreStepper(score = homeScore, onValueChange = { onScoreChange(it, awayScore) })
            }

            Text("VS", color = Color(0xFFD4AF37).copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 4.dp), fontSize = 10.sp)

            // Away Team
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(awayName, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.End)
                ScoreStepper(score = awayScore, onValueChange = { onScoreChange(homeScore, it) })
            }
        }
    }
}

@Composable
fun ScoreStepper(score: Int, onValueChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
        IconButton(
            onClick = { if (score > 0) onValueChange(score - 1) },
            modifier = Modifier.size(28.dp).background(Color.White.copy(0.1f), CircleShape)
        ) {
            Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        }

        Text(
            text = "$score",
            color = Color(0xFFD4AF37),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 10.dp)
        )

        IconButton(
            onClick = { onValueChange(score + 1) },
            modifier = Modifier.size(28.dp).background(Color.White.copy(0.1f), CircleShape)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
    }
}
