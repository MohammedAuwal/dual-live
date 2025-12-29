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
                fontSize = if (isFinal) 20.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isFinal) Color(0xFFFFD700) else Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(timeText, fontSize = 70.sp, fontWeight = FontWeight.Bold, color = Color.White)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Section
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(homeName, fontSize = 24.sp, color = Color.White)
                Text("$homeScore", fontSize = 100.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Row {
                    Button(onClick = { onUpdateHome(1) }) { Text("+") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if(homeScore > 0) onUpdateHome(-1) }) { Text("-") }
                }
            }

            Text("VS", color = Color.Gray, fontSize = 20.sp)

            // Away Section
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(awayName, fontSize = 24.sp, color = Color.White)
                Text("$awayScore", fontSize = 100.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Row {
                    Button(onClick = { onUpdateAway(1) }) { Text("+") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if(awayScore > 0) onUpdateAway(-1) }) { Text("-") }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { isRunning = !isRunning }) {
                Text(if (isRunning) "PAUSE" else "START")
            }
            Button(
                onClick = onSaveAndClose,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text("FINISH & SAVE", color = Color.Black)
            }
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("DISCARD")
            }
        }
    }
}
