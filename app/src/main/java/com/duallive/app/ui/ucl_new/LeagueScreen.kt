package com.duallive.app.ui.ucl_new

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.navigation.NavHostController
import com.duallive.app.ucl2026.model.Ucl26StandingRow

// --- PREMIUM THEME ---
val NavyBackground = Color(0xFF00122E)
val AncientGold = Color(0xFFD4AF37)
val GlassWhite = Color(0x1AFFFFFF)
val DirectGreen = Color(0xFF4CAF50) 
val PlayoffBlue = Color(0xFF2196F3)  
val EliminatedRed = Color(0xFFF44336) 

@Composable
fun LeagueScreen(
    leagueId: Int,
    navController: NavHostController,
    teams: List<Ucl26StandingRow>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("LEAGUE PHASE", color = AncientGold, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("36 TEAMS • SWISS MODEL", color = Color.White.copy(0.6f), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- TABLE HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GlassWhite, RoundedCornerShape(8.dp))
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("POS", Modifier.weight(0.12f), color = AncientGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("CLUB", Modifier.weight(0.48f), color = AncientGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("P", Modifier.weight(0.1f), color = AncientGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("GD", Modifier.weight(0.15f), color = AncientGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("PTS", Modifier.weight(0.15f), color = AncientGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(teams) { index, team ->
                val position = index + 1
                val indicatorColor = when (position) {
                    in 1..8 -> DirectGreen
                    in 9..24 -> PlayoffBlue
                    else -> EliminatedRed
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GlassWhite, RoundedCornerShape(10.dp))
                        .border(0.5.dp, Color.White.copy(0.1f), RoundedCornerShape(10.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(position.toString(), Modifier.weight(0.12f), color = indicatorColor, fontWeight = FontWeight.ExtraBold)
                        Text(team.teamName.uppercase(), Modifier.weight(0.48f), color = Color.White, fontSize = 14.sp)
                        Text(team.matchesPlayed.toString(), Modifier.weight(0.1f), color = Color.White.copy(0.8f))
                        Text(team.goalDifference.toString(), Modifier.weight(0.15f), color = Color.White.copy(0.8f))
                        Text(team.points.toString(), Modifier.weight(0.15f), color = if (position <= 8) AncientGold else Color.White, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
