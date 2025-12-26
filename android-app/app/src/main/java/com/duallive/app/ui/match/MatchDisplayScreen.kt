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
    onClose: () -> Unit
) {
    var seconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }

    // Timer Logic
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            seconds++
        }
    }

    val minutes = seconds / 60
    val displaySeconds = seconds % 60
    val timeText = "%02d:%02d".format(minutes, displaySeconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Dark background for high contrast on TV
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Timer
        Text(
            text = timeText,
            fontSize = 80.sp,
            fontWeight = FontWeight.Bold,
            color = if (isRunning) Color.Green else Color.White
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Team
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(homeName, fontSize = 32.sp, color = Color.White)
                Text("$homeScore", fontSize = 120.sp, fontWeight = FontWeight.Black, color = Color.White)
            }

            Text("VS", fontSize = 24.sp, color = Color.Gray)

            // Away Team
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(awayName, fontSize = 32.sp, color = Color.White)
                Text("$awayScore", fontSize = 120.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        // Control Buttons (Hidden or Small when mirrored)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { isRunning = !isRunning }) {
                Text(if (isRunning) "PAUSE" else "START TIMER")
            }
            Button(onClick = { seconds = 0; isRunning = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                Text("RESET")
            }
            OutlinedButton(onClick = onClose) {
                Text("EXIT DISPLAY", color = Color.White)
            }
        }
    }
}
