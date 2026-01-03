package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.ucl2026.viewmodel.Ucl26ViewModel
import com.duallive.app.ucl2026.model.BracketMatch

@Composable
fun Ucl26BracketScreen(
    viewModel: Ucl26ViewModel,
    onBack: () -> Unit
) {
    val bracketMatches by viewModel.bracketMatches.collectAsState()
    
    val qf = bracketMatches.filter { it.roundName == "Quarter-Final" }
    val sf = bracketMatches.filter { it.roundName == "Semi-Final" }
    val fin = bracketMatches.filter { it.roundName == "FINAL" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00122E))
            .padding(16.dp)
    ) {
        Text("KNOCKOUT PHASE", color = Color(0xFFD4AF37), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if (qf.isNotEmpty()) {
                item { RoundHeader("QUARTER-FINALS", onGenerate = { viewModel.generateSemiFinals() }) }
                items(qf) { match -> BracketMatchCard(match) }
            }
            if (sf.isNotEmpty()) {
                item { RoundHeader("SEMI-FINALS", onGenerate = { viewModel.generateFinal() }) }
                items(sf) { match -> BracketMatchCard(match) }
            }
            if (fin.isNotEmpty()) {
                item { Text("GRAND FINAL", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold) }
                items(fin) { match -> BracketMatchCard(match, isFinal = true) }
            }
            item {
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f))) {
                    Text("BACK TO LEAGUE", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun RoundHeader(title: String, onGenerate: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold)
        Button(onClick = onGenerate, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
            Text("Next Stage", color = Color(0xFF00122E), fontSize = 12.sp)
        }
    }
}

@Composable
fun BracketMatchCard(match: BracketMatch, isFinal: Boolean = false) {
    val win1 = match.isCompleted && match.aggregate1 > match.aggregate2
    val win2 = match.isCompleted && match.aggregate2 > match.aggregate1
    Card(colors = CardDefaults.cardColors(containerColor = Color(0x1AFFFFFF)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            TeamRow(match.team1Name, if(isFinal) match.leg1Score1 else match.aggregate1, win1)
            Spacer(modifier = Modifier.height(8.dp))
            TeamRow(match.team2Name, if(isFinal) match.leg1Score2 else match.aggregate2, win2)
        }
    }
}

@Composable
fun TeamRow(name: String, score: Int?, isWinner: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = name.uppercase(), color = if (isWinner) Color.Green else Color.White, fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!isWinner && score != null) Text("AGG", color = Color(0xFFD4AF37), fontSize = 10.sp, modifier = Modifier.padding(end = 8.dp))
            Text(text = score?.toString() ?: "-", color = if (isWinner) Color.Green else Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
