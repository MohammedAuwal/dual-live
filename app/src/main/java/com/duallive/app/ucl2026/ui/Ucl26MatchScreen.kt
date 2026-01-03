package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.ucl2026.model.Ucl26Match
import com.duallive.app.ucl2026.viewmodel.Ucl26ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ucl26MatchScreen(
    viewModel: Ucl26ViewModel,
    onBack: () -> Unit
) {
    val matches by viewModel.matches.collectAsState()
    val teams by viewModel.standings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MATCH CENTER", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00122E))
            )
        },
        containerColor = Color(0xFF00122E)
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Text("UPDATE EFOOTBALL SCORES", color = Color.White.copy(0.6f), fontSize = 12.sp)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 16.dp)) {
                items(matches) { match ->
                    val homeTeam = teams.find { it.teamId == match.homeTeamId }?.teamName ?: "Unknown"
                    val awayTeam = teams.find { it.teamId == match.awayTeamId }?.teamName ?: "Unknown"
                    MatchItem(match, homeTeam, awayTeam) { h, a -> viewModel.updateScore(match.matchId, h, a) }
                }
            }
        }
    }
}

@Composable
fun MatchItem(match: Ucl26Match, homeName: String, awayName: String, onUpdate: (Int, Int) -> Unit) {
    var hScore by remember { mutableStateOf(match.homeScore?.toString() ?: "") }
    var aScore by remember { mutableStateOf(match.awayScore?.toString() ?: "") }

    Card(colors = CardDefaults.cardColors(containerColor = Color(0x1AFFFFFF)), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(homeName, color = Color.White, modifier = Modifier.weight(1f), fontSize = 14.sp)
            TextField(value = hScore, onValueChange = { if(it.length <= 2) hScore = it }, modifier = Modifier.width(50.dp), colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White), singleLine = true)
            Text("-", color = Color.White, modifier = Modifier.padding(horizontal = 4.dp))
            TextField(value = aScore, onValueChange = { if(it.length <= 2) aScore = it }, modifier = Modifier.width(50.dp), colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White), singleLine = true)
            Text(awayName, color = Color.White, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End, fontSize = 14.sp)
            IconButton(onClick = { 
                val h = hScore.toIntOrNull()
                val a = aScore.toIntOrNull()
                if (h != null && a != null) onUpdate(h, a)
            }) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Confirm", tint = if (match.isPlayed) Color(0xFF4CAF50) else Color.White.copy(0.3f))
            }
        }
    }
}
