package com.duallive.app.ui.team

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val isUcl = leagueType == LeagueType.UCL
    val accentColor = if (isUcl) Color(0xFFE3BC63) else Color.White

    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = Color(0xFF0A192F), // Dark Navy background
        title = { 
            Text(
                text = if (isUcl) "Add Teams to Group" else "Add Teams",
                color = accentColor,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (isUcl) {
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Group Name", color = accentColor.copy(alpha = 0.7f)) },
                        placeholder = { Text("e.g. Group A", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                Text(
                    "Separate names by new line or comma.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
                
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Team Names", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = { Text("Arsenal\nReal Madrid\nBayern...", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (inputText.isNotBlank()) {
                        val teamList = inputText.split("\n", ",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                        
                        val finalGroup = if (isUcl) groupName.trim().ifEmpty { null } else null
                        onSave(teamList, finalGroup)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = if (isUcl) Color.Black else Color(0xFF0A192F)
                )
            ) {
                Text("ADD TEAMS", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { 
                Text("CANCEL", color = Color.White.copy(alpha = 0.7f)) 
            }
        }
    )
}
