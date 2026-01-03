package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
    var teamNames by remember { mutableStateOf(List(36) { "" }) }
    val isComplete = teamNames.all { it.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00122E)) // Navy Background
            .padding(16.dp)
    ) {
        Text(
            "UCL 2026 REGISTRATION",
            color = Color(0xFFD4AF37), // Gold
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Enter 36 teams to generate the Swiss League",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(teamNames) { index, name ->
                OutlinedTextField(
                    value = name,
                    onValueChange = { newName ->
                        val newList = teamNames.toMutableList()
                        newList[index] = newName
                        teamNames = newList
                    },
                    label = { Text("Team ${index + 1}", color = Color.White.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFD4AF37),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { if (isComplete) onTeamsConfirmed(teamNames) },
            enabled = isComplete,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isComplete) Color(0xFFD4AF37) else Color.Gray,
                contentColor = Color(0xFF00122E)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("GENERATE UCL TOURNAMENT", fontWeight = FontWeight.Bold)
        }
    }
}
