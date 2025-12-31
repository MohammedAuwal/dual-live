package com.duallive.app.ui.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    
    // Determine if it's a Gold (UCL/Final) or Silver theme
    val isFinal = stageLabel.contains("Final", ignoreCase = true)
    val accentColor = if (isFinal || stageLabel.isNotEmpty()) Color(0xFFE3BC63) else Color.White

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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header / Stage Label
        if (stageLabel.isNotEmpty()) {
            Text(
                text = if (isFinal) "🏆 ${stageLabel.uppercase()} 🏆" else stageLabel.uppercase(),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Timer Display
        Text(
            text = timeText,
            fontSize = 80.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Main Scoreboard
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Home Team
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(homeName, fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                Spacer(modifier = Modifier.height(16.dp))
                GlassCard(tintColor = accentColor) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$homeScore", fontSize = 90.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Row(modifier = Modifier.padding(bottom = 8.dp)) {
                            IconButton(onClick = { onUpdateHome(1) }) {
                                Text("+", color = accentColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { if(homeScore > 0) onUpdateHome(-1) }) {
                                Text("-", color = accentColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Text("VS", color = accentColor.copy(alpha = 0.5f), fontSize = 20.sp, fontWeight = FontWeight.Black)

            // Away Team
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(awayName, fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                Spacer(modifier = Modifier.height(16.dp))
                GlassCard(tintColor = accentColor) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$awayScore", fontSize = 90.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Row(modifier = Modifier.padding(bottom = 8.dp)) {
                            IconButton(onClick = { onUpdateAway(1) }) {
                                Text("+", color = accentColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { if(awayScore > 0) onUpdateAway(-1) }) {
                                Text("-", color = accentColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Control Buttons
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { isRunning = !isRunning },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.collect(alpha = 0.1f))
            ) {
                Text(if (isRunning) "PAUSE MATCH" else "START MATCH", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onSaveAndClose,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("FINISH", color = Color.White, fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCF6679))
                ) {
                    Text("DISCARD", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
