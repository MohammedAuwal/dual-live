package com.duallive.app.ucl2026.ui

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

// --- THEME ---
val NavyBackground = Color(0xFF00122E)
val AncientGold = Color(0xFFD4AF37)
val GlassWhite = Color(0x1AFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ucl26RegistrationScreen(
    onTeamsConfirmed: (List<String>) -> Unit
) {
    var rawInput by remember { mutableStateOf("") }
    val teamList = rawInput.split("\n").filter { it.isNotBlank() }
    val currentCount = teamList.size
    val requiredCount = 36

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(20.dp)
    ) {
        Text(
            text = "UCL 2026 SETUP",
            color = AncientGold,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp
        )
        
        Text(
            text = "Paste your 36 eFootball teams below",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Glassmorphic Input
        OutlinedTextField(
            value = rawInput,
            onValueChange = { rawInput = it },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(GlassWhite, RoundedCornerShape(16.dp))
                .border(1.dp, AncientGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            placeholder = { Text("One team per line...", color = Color.Gray) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = AncientGold,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Proceed Button
        Button(
            onClick = { if (currentCount == requiredCount) onTeamsConfirmed(teamList) },
            enabled = currentCount == requiredCount,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AncientGold,
                disabledContainerColor = Color.Gray.copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(
                "START TOURNAMENT ($currentCount/$requiredCount)",
                color = if (currentCount == requiredCount) NavyBackground else Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
