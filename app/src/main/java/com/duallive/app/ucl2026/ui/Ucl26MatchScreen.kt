package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
    val currentRound by viewModel.currentRound.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF00122E))) {
        // Updated to stable TopAppBar
        TopAppBar(
            title = { Text("Round $currentRound Fixtures", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        LazyColumn(
            modifier = Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(matches) { match ->
                MatchResultCard(
                    homeName = "Team ${match.homeTeamId}", 
                    awayName = "Team ${match.awayTeamId}",
                    homeScore = match.homeScore,
                    awayScore = match.awayScore,
                    isPlayed = match.isPlayed,
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
}

@Composable
fun MatchResultCard(
    homeName: String, 
    awayName: String, 
    homeScore: Int, 
    awayScore: Int, 
    isPlayed: Boolean,
    onScoreChange: (Int, Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x1AFFFFFF)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(homeName, color = Color.White, modifier = Modifier.weight(1f))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                ScoreBox(homeScore)
                Text("-", color = Color.White, modifier = Modifier.padding(horizontal = 8.dp))
                ScoreBox(awayScore)
            }

            Text(awayName, color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        }
    }
}

@Composable
fun ScoreBox(score: Int) {
    Box(
        modifier = Modifier.size(36.dp).background(Color.White.copy(0.1f), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("$score", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold)
    }
}
