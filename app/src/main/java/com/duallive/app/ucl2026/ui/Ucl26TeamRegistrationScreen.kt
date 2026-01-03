package com.duallive.app.ui.ucl_new

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.viewmodel.TeamViewModel

// --- THEME COLORS ---
val NavyBackground = Color(0xFF00122E)
val AncientGold = Color(0xFFD4AF37)
val GlassWhite = Color(0x1AFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewUclTeamRegistrationScreen(
    teamViewModel: TeamViewModel,
    onTeamsRegistered: (Int) -> Unit
) {
    var rawInput by remember { mutableStateOf("") }
    
    // Logic to count lines (teams)
    val teamList = rawInput.split("\n").filter { it.isNotBlank() }
    val currentCount = teamList.size
    val requiredCount = 36

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(20.dp)
    ) {
        // --- HEADER ---
        Text(
            text = "UCL 2026 REGISTRATION",
            color = AncientGold,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp
        )
        
        Text(
            text = "Bulk import teams for your eFootball league",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        // --- GLASSMORPHIC INPUT BOX ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(GlassWhite, RoundedCornerShape(16.dp))
                .border(1.dp, AncientGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            TextField(
                value = rawInput,
                onValueChange = { rawInput = it },
                modifier = Modifier.fillMaxSize(),
                placeholder = { 
                    Text(
                        "Paste your 36 teams here...\n(One team per line)", 
                        color = Color.Gray 
                    ) 
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = AncientGold,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- STATUS & PROCEED BUTTON ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Teams Detected: $currentCount",
                    color = if (currentCount == requiredCount) Color.Green else Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (currentCount < requiredCount) "Need ${requiredCount - currentCount} more" 
                           else if (currentCount > requiredCount) "Too many teams!" 
                           else "Ready to generate league",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = { 
                    if (currentCount == requiredCount) {
                        // Here we simulate saving and returning a leagueId of 1 for now
                        onTeamsRegistered(1) 
                    }
                },
                enabled = currentCount == requiredCount,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AncientGold,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(50.dp)
            ) {
                Text(
                    "START LEAGUE", 
                    color = NavyBackground, 
                    fontWeight = FontWeight.Black 
                )
            }
        }
    }
}