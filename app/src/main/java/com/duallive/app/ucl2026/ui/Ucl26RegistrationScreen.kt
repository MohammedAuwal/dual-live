package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ucl26RegistrationScreen(onTeamsConfirmed: (List<String>) -> Unit) {
    var teamList by remember { mutableStateOf(emptyList<String>()) }
    var showDialog by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF00122E)).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("UCL Registration", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("${teamList.size} / 36 Teams Added", color = Color(0xFFD4AF37), fontSize = 14.sp)
            }
            Row {
                if (teamList.size == 36) {
                    IconButton(onClick = { teamList = teamList.shuffled() }, modifier = Modifier.padding(end = 8.dp).background(Color.White.copy(0.1f), RoundedCornerShape(8.dp))) {
                        Icon(Icons.Default.Shuffle, contentDescription = null, tint = Color.White)
                    }
                }
                IconButton(onClick = { showDialog = true }, modifier = Modifier.background(Color(0xFFD4AF37), RoundedCornerShape(8.dp))) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF00122E))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (teamList.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No Teams added yet.\nTap + to start.", color = Color.White.copy(0.4f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(teamList.asReversed()) { team ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0x1AFFFFFF)), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(team, color = Color.White, fontWeight = FontWeight.Medium)
                            IconButton(onClick = { teamList = teamList - team }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(0.7f))
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { onTeamsConfirmed(teamList) },
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 8.dp),
            enabled = teamList.size == 36,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37), disabledContainerColor = Color.Gray),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("GENERATE SWISS LEAGUE", color = Color(0xFF00122E), fontWeight = FontWeight.Bold)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color(0xFF0A192F),
            title = { Text("Add Teams", color = Color.White) },
            text = {
                Column {
                    Text("Paste names (one per line) or comma separated.", color = Color.White.copy(0.6f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        placeholder = { Text("Example: Real Madrid, Man City...", color = Color.White.copy(0.3f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD4AF37),
                            unfocusedBorderColor = Color.White.copy(0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val newTeams = inputText.split("\n", ",").map { it.trim() }.filter { it.isNotBlank() }
                    teamList = (teamList + newTeams).distinct().take(36)
                    inputText = ""
                    showDialog = false
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37))) {
                    Text("ADD", color = Color(0xFF00122E))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("CANCEL", color = Color.White)
                }
            }
        )
    }
}
