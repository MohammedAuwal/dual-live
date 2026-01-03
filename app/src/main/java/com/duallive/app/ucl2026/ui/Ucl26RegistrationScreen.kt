package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Ucl26RegistrationScreen(
    onTeamsConfirmed: (List<String>) -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    val lines = textInput.lines().filter { it.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00122E))
            .padding(24.dp)
    ) {
        Text("UCL 2026 REGISTRATION", color = Color(0xFFD4AF37), fontSize = 22.sp)
        Text("PASTE 36 TEAMS BELOW", color = Color.White.copy(0.6f), fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = textInput,
            onValueChange = { textInput = it },
            modifier = Modifier.fillMaxWidth().weight(1f),
            placeholder = { Text("Team 1\nTeam 2...", color = Color.Gray) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0x1AFFFFFF),
                focusedContainerColor = Color(0x1AFFFFFF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Button(
            onClick = { onTeamsConfirmed(lines) },
            enabled = lines.size == 36,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD4AF37),
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                if (lines.size == 36) "INITIALIZE LEAGUE" else "ENTER 36 TEAMS (${lines.size}/36)",
                color = Color(0xFF00122E)
            )
        }
    }
}
