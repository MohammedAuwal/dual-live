package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
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
import com.duallive.app.ucl2026.viewmodel.Ucl26ViewModel

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
                title = { Text("ROUND $round / 8", color = Color(0xFFD4AF37)) },
                actions = {
                    IconButton(onClick = { viewModel.nextRound() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Next Round", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00122E))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("new_ucl_matches") },
                containerColor = Color(0xFFD4AF37)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Scores", tint = Color(0xFF00122E))
            }
        },
        containerColor = Color(0xFF00122E)
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            // Table Header
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0x1AFFFFFF), RoundedCornerShape(8.dp)).padding(8.dp)
            ) {
                Text("POS", Modifier.weight(0.12f), color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold)
                Text("TEAM", Modifier.weight(0.5f), color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold)
                Text("GD", Modifier.weight(0.15f), color = Color(0xFFD4AF37), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Text("PTS", Modifier.weight(0.15f), color = Color(0xFFD4AF37), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 8.dp)) {
                itemsIndexed(teams) { index, team ->
                    val pos = index + 1
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0x1AFFFFFF))) {
                        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("$pos", Modifier.weight(0.12f), color = if(pos <= 8) Color.Green else Color.White)
                            Text(team.teamName.uppercase(), Modifier.weight(0.5f), color = Color.White, fontSize = 13.sp)
                            Text("${team.goalDifference}", Modifier.weight(0.15f), color = Color.White, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Text("${team.points}", Modifier.weight(0.15f), color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
