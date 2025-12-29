package com.duallive.app.ui.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MatchDisplayScreen(
    homeName: String,
    awayName: String,
    homeScore: Int,
    awayScore: Int,
    onUpdateHome: (Int) -> Unit,
    onUpdateAway: (Int) -> Unit,
    onSaveAndClose: () -> Unit,
    onCancel: () -> Unit,
    stageLabel: String = ""
) {
    var seconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    val isFinal = stageLabel.contains("Final", ignoreCase = true)

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            seconds++
        }
    }

    val timeText = "%02d:%02d".format(seconds / 60, seconds % 60)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (stageLabel.isNotEmpty()) {
            Text(
                text = if (isFinal) "🏆 $stageLabel 🏆" else stageLabel.uppercase(),
                fontSize = if (isFinal) 24.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isFinal) Color(0xFFFFD700) else Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Timer Display
        Text(timeText, fontSize = 80.sp, fontWeight = FontWeight.Bold, color = Color.White)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Section
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(homeName, fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Medium)
                Text("$homeScore", fontSize = 110.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Row {
                    Button(onClick = { onUpdateHome(1) }) { Text("+", fontSize = 20.sp) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if(homeScore > 0) onUpdateHome(-1) }) { Text("-", fontSize = 20.sp) }
                }
            }

            Text("VS", color = Color.Gray, fontSize = 24.sp, fontWeight = FontWeight.Bold)

            // Away Section
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(awayName, fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Medium)
                Text("$awayScore", fontSize = 110.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Row {
                    Button(onClick = { onUpdateAway(1) }) { Text("+", fontSize = 20.sp) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if(awayScore > 0) onUpdateAway(-1) }) { Text("-", fontSize = 20.sp) }
                }
            }
        }

        // Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), 
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(containerColor = if (isRunning) Color.DarkGray else Color.Blue)
            ) {
                Text(if (isRunning) "PAUSE" else "START")
            }
            
            Button(
                onClick = onSaveAndClose,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("FINISH", color = Color.White)
            }
            
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
            ) {
                Text("DISCARD", color = Color.White)
            }
        }
    }
}
