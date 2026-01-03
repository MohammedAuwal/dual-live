package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Ucl26BulkEntryScreen(onTeamsConfirmed: (List<String>) -> Unit) {
    var rawInput by remember { mutableStateOf("") }
    val teamList = rawInput.split("\n").filter { it.isNotBlank() }
    val count = teamList.size

    val navyBackground = Color(0xFF00122E)
    val goldAncient = Color(0xFFD4AF37)
    val glassWhite = Color(0x1AFFFFFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(navyBackground)
            .padding(20.dp)
    ) {
        Text(
            text = "TOURNAMENT REGISTRATION",
            color = goldAncient,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp
        )
        Text(
            text = "PASTE 36 TEAMS FROM YOUR COMMUNITY",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // The Glassmorphic Input Box
        OutlinedTextField(
            value = rawInput,
            onValueChange = { rawInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(glassWhite, RoundedCornerShape(12.dp))
                .border(1.dp, goldAncient.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            placeholder = { Text("Paste names here...", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = goldAncient,
                unfocusedBorderColor = Color.Transparent,
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Premium Gold Button
        Button(
            onClick = { if (count == 36) onTeamsConfirmed(teamList) },
            enabled = count == 36,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .border(2.dp, if (count == 36) goldAncient else Color.DarkGray, RoundedCornerShape(30.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (count == 36) goldAncient.copy(alpha = 0.1f) else Color.Transparent,
                contentColor = if (count == 36) goldAncient else Color.DarkGray
            ),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(
                text = "VALIDATE & START ($count/36)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
