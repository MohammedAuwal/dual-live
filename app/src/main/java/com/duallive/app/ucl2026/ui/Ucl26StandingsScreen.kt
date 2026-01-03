package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.ucl2026.model.Ucl26StandingRow

// Theme Colors
val NavyBackground = Color(0xFF001220)
val GoldAccent = Color(0xFFC5A059)
val GlassWhite = Color(0x1AFFFFFF)
val DirectGreen = Color(0xFF4CAF50)
val PlayoffYellow = Color(0xFFFFC107)
val EliminatedRed = Color(0xFFF44336)

@Composable
fun Ucl26StandingsScreen(standings: List<Ucl26StandingRow>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "UEFA CHAMPIONS LEAGUE",
            color = GoldAccent,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "LEAGUE PHASE 2025/26",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GlassWhite, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text("Pos", Modifier.weight(0.1f), color = GoldAccent, fontWeight = FontWeight.Bold)
            Text("Team", Modifier.weight(0.4f), color = GoldAccent, fontWeight = FontWeight.Bold)
            Text("P", Modifier.weight(0.1f), color = GoldAccent, fontWeight = FontWeight.Bold)
            Text("GD", Modifier.weight(0.15f), color = GoldAccent, fontWeight = FontWeight.Bold)
            Text("Pts", Modifier.weight(0.15f), color = GoldAccent, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            itemsIndexed(standings) { index, team ->
                StandingRowItem(index + 1, team)
            }
        }
    }
}

@Composable
fun StandingRowItem(pos: Int, team: Ucl26StandingRow) {
    val zoneColor = when (pos) {
        in 1..8 -> DirectGreen
        in 9..24 -> PlayoffYellow
        else -> EliminatedRed
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassWhite, RoundedCornerShape(8.dp))
            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            // Position with Zone Indicator
            Text(
                text = pos.toString(),
                modifier = Modifier.weight(0.1f),
                color = zoneColor,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = team.teamName,
                modifier = Modifier.weight(0.4f),
                color = Color.White,
                fontWeight = if (pos <= 8) FontWeight.Bold else FontWeight.Normal
            )
            
            Text(team.matchesPlayed.toString(), Modifier.weight(0.1f), color = Color.LightGray)
            Text(team.goalDifference.toString(), Modifier.weight(0.15f), color = Color.LightGray)
            Text(
                text = team.points.toString(),
                modifier = Modifier.weight(0.15f),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
