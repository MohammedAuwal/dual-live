package com.duallive.app.ui.team

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.LeagueType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTeamScreen(
    leagueType: LeagueType, 
    onSave: (List<String>, String?) -> Unit, 
    onCancel: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var groupName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(if (leagueType == LeagueType.UCL) "Add Teams to Group" else "Add Teams") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (leagueType == LeagueType.UCL) {
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Group (e.g. Group A)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Text(
                    "Enter team names below. Separate by new line or comma.",
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
                    val teamList = inputText.split("\n", ",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                    
                    // If Classic, groupName is null. If UCL, we use the text.
                    val finalGroup = if (leagueType == LeagueType.UCL) groupName.trim().ifEmpty { null } else null
                    onSave(teamList, finalGroup)
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
