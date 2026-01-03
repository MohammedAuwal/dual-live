package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.* // This includes Scaffold and Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.ucl2026.viewmodel.Ucl26ViewModel

// REMOVED @OptIn to prevent NonExistentClass error if the compiler is struggling with it
@Composable
fun Ucl26BracketScreen(viewModel: Ucl26ViewModel, onBack: () -> Unit) {
    val bracketMatches by viewModel.bracketMatches.collectAsState()
    val r16Matches by viewModel.r16Matches.collectAsState()
    val standings by viewModel.standings.collectAsState()
    val top8 = standings.take(8)

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF00122E))) {
        // Using a standard Row instead of TopAppBar to avoid Experimental API requirements
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                "KNOCKOUT PHASE", 
                color = Color(0xFFD4AF37), 
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            item {
                Text("SEEDED TEAMS (WAITING IN R16)", color = Color.White.copy(0.6f), fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    top8.take(4).forEach { team -> SeededChip(team.teamName) }
                }
            }

            if (bracketMatches.isEmpty()) {
                item {
                    Button(
                        onClick = { viewModel.generatePlayoffDraw() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("DRAW PLAY-OFF MATCHES", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                items(bracketMatches) { match ->
                    BracketMatchItem(match.team1Name, match.team2Name, match.aggregate1, match.aggregate2)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun SeededChip(name: String) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFD4AF37).copy(0.5f)),
        modifier = Modifier.width(80.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(12.dp))
            Text(name, color = Color.White, fontSize = 10.sp, maxLines = 1)
        }
    }
}

@Composable
fun BracketMatchItem(t1: String, t2: String, s1: Int, s2: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x1AFFFFFF)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(t1, color = Color.White, fontSize = 14.sp)
                Text("$s1", color = if(s1 > s2) Color.Green else Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
