package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.duallive.app.ucl2026.viewmodel.Ucl26ViewModel

// PRIVATE COLORS TO PREVENT AMBIGUITY
private val LocalNavy = Color(0xFF00122E)
private val LocalGold = Color(0xFFD4AF37)
private val LocalGlass = Color(0x1AFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ucl26LeagueScreen(
    leagueId: Int,
    navController: NavHostController,
    viewModel: Ucl26ViewModel
) {
    val teams by viewModel.standings.collectAsState()
    val round by viewModel.currentRound.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (round <= 8) "ROUND $round / 8" else "LEAGUE FINISHED", 
                        color = LocalGold, 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                actions = {
                    IconButton(onClick = { 
                        if (round < 8) {
                            viewModel.nextRound()
                        } else {
                            // After Round 8, generate bracket and move to bracket screen
                            viewModel.nextRound() 
                            navController.navigate("new_ucl_bracket")
                        }
                    }) {
                        // Change icon to a tree/bracket icon if round 8 is reached
                        Icon(
                            imageVector = if (round < 8) Icons.Default.Refresh else Icons.Default.AccountTree, 
                            contentDescription = "Next Round", 
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LocalNavy)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("new_ucl_matches") },
                containerColor = LocalGold
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Scores", tint = LocalNavy)
            }
        },
        containerColor = LocalNavy
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LocalGlass, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text("POS", Modifier.weight(0.12f), color = LocalGold, fontWeight = FontWeight.Bold)
                Text("TEAM", Modifier.weight(0.5f), color = LocalGold, fontWeight = FontWeight.Bold)
                Text("GD", Modifier.weight(0.15f), color = LocalGold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Text("PTS", Modifier.weight(0.15f), color = LocalGold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 8.dp)) {
                itemsIndexed(teams) { index, team ->
                    val pos = index + 1
                    Card(colors = CardDefaults.cardColors(containerColor = LocalGlass)) {
                        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            // Top 8 highlight for qualification
                            Text("$pos", Modifier.weight(0.12f), color = if(pos <= 8) Color.Green else Color.White)
                            Text(team.teamName.uppercase(), Modifier.weight(0.5f), color = Color.White, fontSize = 13.sp)
                            Text("${team.goalDifference}", Modifier.weight(0.15f), color = Color.White, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Text("${team.points}", Modifier.weight(0.15f), color = LocalGold, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
