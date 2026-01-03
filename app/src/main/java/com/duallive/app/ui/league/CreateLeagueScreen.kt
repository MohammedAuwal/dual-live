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
    onSave: (String, String, Boolean, LeagueType) -> Unit,
    onBack: () -> Unit // Added back navigation
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var isHomeAndAway by remember { mutableStateOf(false) }
    
    // Safety: Determine theme based on the 3 types
    val accentColor = when(preselectedType) {
        LeagueType.UCL -> Color(0xFFE3BC63)
        LeagueType.SWISS -> Color(0xFFD4AF37) // Gold for Swiss
        else -> Color.White
    }

    val titleText = when(preselectedType) {
        LeagueType.UCL -> "New UCL Group"
        LeagueType.SWISS -> "New UCL Swiss"
        else -> "New Classic League"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Added Row for Back Button and Title
        Row(verticalAlignment = Alignment.CenterVertically) {
             TextButton(onClick = onBack) {
                 Text("< BACK", color = Color.White.copy(0.7f))
             }
             Spacer(modifier = Modifier.width(8.dp))
             Text(
                text = titleText,
                color = accentColor,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        GlassCard(tintColor = accentColor) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

                // Hide Home & Away for Swiss as it uses a single table format
                if (preselectedType != LeagueType.SWISS) {
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
                } else {
                    Text(
                        "36 Teams • 8 Rounds • Swiss System",
                        color = accentColor.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onSave(name, desc, isHomeAndAway, preselectedType) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = name.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = accentColor,
                contentColor = if (accentColor == Color.White) Color(0xFF0A192F) else Color.Black
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Text("CREATE TOURNAMENT", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        }
    }
}
