package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
fun Ucl26BracketScreen(viewModel: Ucl26ViewModel, onBack: () -> Unit) {
    val bracketMatches by viewModel.bracketMatches.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF00122E))) {
        TopAppBar(
            title = { Text("Knockout Phase", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        if (bracketMatches.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Bracket will be generated\nafter the Swiss Stage ends.",
                    color = Color.White.copy(0.5f),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Round of 16
                item { BracketHeader("Round of 16") }
                val r16 = bracketMatches.filter { it.roundName == "R16" }
                items(r16.size) { index ->
                    val match = r16[index]
                    BracketMatchCard(
                        team1 = match.team1Name,
                        team2 = match.team2Name,
                        score1 = match.leg1Score1,
                        score2 = match.leg1Score2,
                        onScoreChange = { s1, s2 ->
                            // We will need a updateBracketScore function in ViewModel
                            viewModel.updateBracketScore(match.id, s1, s2)
                        }
                    )
                }

                // Quarter-Finals
                item { BracketHeader("Quarter-Finals") }
                val qf = bracketMatches.filter { it.roundName == "QF" }
                items(qf.size) { index ->
                    val match = qf[index]
                    BracketMatchCard(
                        team1 = match.team1Name,
                        team2 = match.team2Name,
                        score1 = match.leg1Score1,
                        score2 = match.leg1Score2,
                        onScoreChange = { s1, s2 ->
                            viewModel.updateBracketScore(match.id, s1, s2)
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun BracketHeader(title: String) {
    Text(
        text = title,
        color = Color(0xFFD4AF37),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun BracketMatchCard(
    team1: String,
    team2: String,
    score1: Int,
    score2: Int,
    onScoreChange: (Int, Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.05f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Team 1 Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(team1, color = Color.White, modifier = Modifier.weight(1f))
                BracketStepper(score = score1, onValueChange = { onScoreChange(it, score2) })
            }
            
            Divider(color = Color.White.copy(0.1f), modifier = Modifier.padding(vertical = 8.dp))
            
            // Team 2 Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(team2, color = Color.White, modifier = Modifier.weight(1f))
                BracketStepper(score = score2, onValueChange = { onScoreChange(score1, it) })
            }
        }
    }
}

@Composable
fun BracketStepper(score: Int, onValueChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { if (score > 0) onValueChange(score - 1) },
            modifier = Modifier.size(24.dp).background(Color.White.copy(0.1f), CircleShape)
        ) {
            Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
        }
        Text(
            text = "$score",
            color = Color(0xFFD4AF37),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp),
            fontSize = 16.sp
        )
        IconButton(
            onClick = { onValueChange(score + 1) },
            modifier = Modifier.size(24.dp).background(Color.White.copy(0.1f), CircleShape)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
        }
    }
}
