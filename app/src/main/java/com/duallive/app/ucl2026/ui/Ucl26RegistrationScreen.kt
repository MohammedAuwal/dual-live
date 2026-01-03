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
import com.duallive.app.ucl2026.viewmodel.Ucl26ViewModel

@Composable
fun Ucl26RegistrationScreen(
    onTeamsConfirmed: (List<String>) -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00122E))
            .padding(24.dp)
    ) {
        Text("PASTE 36 TEAMS", color = Color(0xFFD4AF37), fontSize = 20.dp.value.sp)
        Text("One team per line", color = Color.White.copy(0.6f), fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = textInput,
            onValueChange = { textInput = it },
            modifier = Modifier.fillMaxWidth().weight(1f),
            placeholder = { Text("Team 1\nTeam 2...") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0x1AFFFFFF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Button(
            onClick = {
                val lines = textInput.lines().filter { it.isNotBlank() }
                if (lines.size == 36) {
                    onTeamsConfirmed(lines)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("INITIALIZE TOURNAMENT", color = Color(0xFF00122E))
        }
    }
}
