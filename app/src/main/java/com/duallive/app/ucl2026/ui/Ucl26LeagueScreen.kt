package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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

// --- THEME ---
val NavyBackground = Color(0xFF00122E)
val AncientGold = Color(0xFFD4AF37)
val GlassWhite = Color(0x1AFFFFFF)
val DirectGreen = Color(0xFF4CAF50) 
val PlayoffBlue = Color(0xFF2196F3)  
val EliminatedRed = Color(0xFFF44336) 

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ucl26LeagueScreen(
    leagueId: Int,
    navController: NavHostController,
    teams: List<Ucl26StandingRow>
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("new_ucl_matches") },
                containerColor = AncientGold,
                contentColor = NavyBackground,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Enter Scores")
            }
        },
        containerColor = NavyBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // --- HEADER ---
            Text(
                text = "LEAGUE PHASE", 
                color = AncientGold, 
                fontSize = 24.sp, 
                fontWeight = FontWeight.Black
            )
            Text(
                text = "36 TEAMS • AUTOMATIC UPDATES", 
                color = Color.White.copy(0.6f), 
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- TABLE HEADER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassWhite, RoundedCornerShape(8.dp))
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("POS", Modifier.weight(0.12f), color = AncientGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("CLUB", Modifier.weight(0.48f), color = AncientGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("P", Modifier.weight(0.1f), color = AncientGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Text("GD", Modifier.weight(0.15f), color = AncientGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Text("PTS", Modifier.weight(0.15f), color = AncientGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- STANDINGS ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(teams) { index, team ->
                    val pos = index + 1
                    val indicator = when (pos) {
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
                            Text(
                                text = pos.toString(), 
                                modifier = Modifier.weight(0.12f), 
                                color = indicator, 
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = team.teamName.uppercase(), 
                                modifier = Modifier.weight(0.48f), 
                                color = Color.White, 
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                            Text(
                                text = team.matchesPlayed.toString(), 
                                modifier = Modifier.weight(0.1f), 
                                color = Color.White.copy(0.7f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Text(
                                text = (if(team.goalDifference > 0) "+" else "") + team.goalDifference.toString(), 
                                modifier = Modifier.weight(0.15f), 
                                color = Color.White.copy(0.7f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Text(
                                text = team.points.toString(), 
                                modifier = Modifier.weight(0.15f), 
                                color = if (pos <= 8) AncientGold else Color.White, 
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
