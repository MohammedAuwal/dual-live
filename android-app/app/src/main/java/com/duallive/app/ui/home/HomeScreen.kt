package com.duallive.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.ui.components.GlassCard

@Composable
fun HomeScreen(
    onCreateLeague: () -> Unit,
    onViewLeagues: () -> Unit,
    onJoinSubmit: (String) -> Unit
) {
    var showJoinDialog by remember { mutableStateOf(false) }
    var leagueIdInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF040B25), Color(0xFF0A194E), Color(0xFF040B25))
    )

    // --- Join League Dialog ---
    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { 
                showJoinDialog = false 
                isError = false
            },
            title = { Text("Join League", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter the unique ID shared by the league creator.", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = leagueIdInput,
                        onValueChange = { 
                            leagueIdInput = it
                            isError = false 
                        },
                        label = { Text("League ID") },
                        placeholder = { Text("e.g. DL-8821") },
                        isError = isError,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            if (isError) Text("Please enter a valid ID")
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        if (leagueIdInput.isNotBlank()) {
                            onJoinSubmit(leagueIdInput)
                            showJoinDialog = false
                            leagueIdInput = "" 
                        } else {
                            isError = true
                        }
                    }
                ) { Text("Join Now") }
            },
            dismissButton = {
                TextButton(onClick = { showJoinDialog = false }) { Text("Cancel") }
            }
        )
    }

    // --- Main Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Text("Welcome to DuaLive!", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Manage • Play • Stream • Trade", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)

        Spacer(modifier = Modifier.height(30.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            GlassCard(modifier = Modifier.weight(1f), onClick = onCreateLeague) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.Cyan, modifier = Modifier.size(40.dp))
                Spacer(Modifier.height(8.dp))
                Text("Create League", color = Color.White, fontWeight = FontWeight.Bold)
            }
            GlassCard(modifier = Modifier.weight(1f), onClick = { showJoinDialog = true }) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                Spacer(Modifier.height(8.dp))
                Text("Join League", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text("Premium Features", color = Color.White, modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GlassCard(modifier = Modifier.weight(1f), onClick = onCreateLeague) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow)
                Text("UCL", color = Color.White, fontSize = 12.sp)
            }
            GlassCard(modifier = Modifier.weight(1f), onClick = onCreateLeague) {
                Icon(Icons.Default.List, contentDescription = null, tint = Color.Cyan)
                Text("Classic", color = Color.White, fontSize = 12.sp)
            }
            GlassCard(modifier = Modifier.weight(1f), onClick = onViewLeagues) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                Text("My Leagues", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}
