package com.duallive.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.ui.components.GlassCard

@Composable
fun HomeScreen(
    onNavigateToClassic: () -> Unit,
    onNavigateToUCL: () -> Unit,
    onNavigateToNewUCL: () -> Unit,
    onJoinSubmit: (String) -> Unit
) {
    var joinCode by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 20.dp)) {
                Text(
                    text = "Dual Live",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Select your tournament style",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
            }
        }

        item {
            GlassCard(
                modifier = Modifier.clickable { onNavigateToClassic() },
                tintColor = Color.White
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Classic League", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Round-robin points table", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                    }
                }
            }
        }

        item {
            GlassCard(
                modifier = Modifier.clickable { onNavigateToUCL() },
                tintColor = Color(0xFFE3BC63)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFE3BC63), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("UCL League", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Groups & Knockout format", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                    }
                }
            }
        }

        item {
            // FIX: Explicitly handling the click inside a Box to ensure the lambda triggers
            GlassCard(
                tintColor = Color(0xFF00BFFF)
            ) {
                Box(modifier = Modifier.fillMaxWidth().clickable { onNavigateToNewUCL() }) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp), 
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFF00BFFF), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("New UCL League", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Swiss / Dynamic league tracking", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        item {
            GlassCard {
                Text("Join a Tournament", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                OutlinedTextField(
                    value = joinCode,
                    onValueChange = { joinCode = it },
                    placeholder = { Text("Enter Invite Code", color = Color.White.copy(alpha = 0.4f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onJoinSubmit(joinCode) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = joinCode.length > 4,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    Text("Join Now", color = Color.White)
                }
            }
        }
    }
}
