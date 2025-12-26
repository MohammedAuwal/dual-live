package com.duallive.app.ui.team

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTeamScreen(onSave: (List<String>) -> Unit, onCancel: () -> Unit) {
    var inputText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Add Teams") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Enter team names below. Separate multiple teams by a new line or comma.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Team Names") },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    placeholder = { Text("Team A\nTeam B\nTeam C...") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                if (inputText.isNotBlank()) {
                    // Split by New Line OR Comma, then clean up spaces
                    val teamList = inputText.split("\n", ",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                    onSave(teamList)
                }
            }) {
                Text("Add All")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    )
}
