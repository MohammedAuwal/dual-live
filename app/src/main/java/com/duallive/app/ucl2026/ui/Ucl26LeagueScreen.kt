package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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

@Composable
fun Ucl26LeagueScreen(
    leagueId: Int,
    viewModel: Ucl26ViewModel,
    onNavigateToMatches: () -> Unit,
    onNavigateToBracket: () -> Unit
) {
    val standings by viewModel.standings.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF00122E)).padding(16.dp)) {
        Text("UCL SWISS STANDINGS", color = Color(0xFFD4AF37), fontSize = 22.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Key/Legend
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            LegendItem("TOP 8 (R16)", Color(0xFF4CAF50))
            LegendItem("PLAY-OFFS", Color(0xFF2196F3))
            LegendItem("OUT", Color(0xFFF44336))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Table Header
        Row(modifier = Modifier.fillMaxWidth().background(Color.White.copy(0.05f)).padding(8.dp)) {
            Text("Pos", color = Color.White.copy(0.6f), modifier = Modifier.width(35.dp), fontSize = 12.sp)
            Text("Team", color = Color.White.copy(0.6f), modifier = Modifier.weight(1f), fontSize = 12.sp)
            Text("GD", color = Color.White.copy(0.6f), modifier = Modifier.width(35.dp), fontSize = 12.sp)
            Text("Pts", color = Color.White.copy(0.6f), modifier = Modifier.width(35.dp), fontSize = 12.sp)
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(standings) { index, team ->
                val pos = index + 1
                val rowColor = when {
                    pos <= 8 -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                    pos <= 24 -> Color(0xFF2196F3).copy(alpha = 0.1f)
                    else -> Color(0xFFF44336).copy(alpha = 0.1f)
                }
                
                val indicatorColor = when {
                    pos <= 8 -> Color(0xFF4CAF50)
                    pos <= 24 -> Color(0xFF2196F3)
                    else -> Color(0xFFF44336)
                }

                Surface(
                    color = rowColor,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.width(4.dp).height(20.dp).background(indicatorColor))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("$pos", color = Color.White, modifier = Modifier.width(25.dp), fontWeight = FontWeight.Bold)
                        Text(team.teamName, color = Color.White, modifier = Modifier.weight(1f))
                        Text("${team.goalDifference}", color = Color.White.copy(0.7f), modifier = Modifier.width(35.dp))
                        Text("${team.points}", color = Color(0xFFD4AF37), modifier = Modifier.width(35.dp), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Navigation Buttons
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onNavigateToMatches, 
                modifier = Modifier.weight(1f), 
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("MATCHES", color = Color(0xFF00122E), fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onNavigateToBracket, 
                modifier = Modifier.weight(1f), 
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("BRACKET", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LegendItem(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, color = Color.White.copy(0.6f), fontSize = 10.sp)
    }
}
