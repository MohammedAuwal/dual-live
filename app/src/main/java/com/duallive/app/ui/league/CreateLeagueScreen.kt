package com.duallive.app.ui.league

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.data.entity.LeagueType
import com.duallive.app.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLeagueScreen(
    preselectedType: LeagueType,
    onSave: (String, String, Boolean, LeagueType) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var isHomeAndAway by remember { mutableStateOf(false) }
    
    // Theme setup
    val accentColor = if (preselectedType == LeagueType.UCL) Color(0xFFE3BC63) else Color.White
    val titleText = if (preselectedType == LeagueType.UCL) "New UCL Tournament" else "New Classic League"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = titleText,
            color = accentColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )

        GlassCard(tintColor = accentColor) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("League Name", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Description Field
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description (Optional)", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Format Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Home & Away", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(
                            "Teams play each other twice",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = isHomeAndAway,
                        onCheckedChange = { isHomeAndAway = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = accentColor,
                            checkedTrackColor = accentColor.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Glass Action Button
        Button(
            onClick = { onSave(name, desc, isHomeAndAway, preselectedType) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = name.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = accentColor,
                contentColor = if (preselectedType == LeagueType.UCL) Color.Black else Color(0xFF0A192F)
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Text("CREATE TOURNAMENT", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        }
    }
}
