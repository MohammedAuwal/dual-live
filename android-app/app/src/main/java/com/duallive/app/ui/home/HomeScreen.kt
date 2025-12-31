package com.duallive.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.ui.components.GlassCard

@Composable
fun HomeScreen(
    onCreateLeague: () -> Unit,
    onViewLeagues: () -> Unit,
    onJoinLeague: () -> Unit
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF040B25), Color(0xFF0A194E), Color(0xFF040B25))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // Header
        Text("Welcome to DuaLive!", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Manage • Play • Stream • Trade", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)

        Spacer(modifier = Modifier.height(30.dp))

        // Main Actions
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            GlassCard(modifier = Modifier.weight(1f), onClick = onCreateLeague) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.Cyan, modifier = Modifier.size(40.dp))
                Spacer(Modifier.height(8.dp))
                Text("Create League", color = Color.White, fontWeight = FontWeight.Bold)
            }
            GlassCard(modifier = Modifier.weight(1f), onClick = onJoinLeague) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                Spacer(Modifier.height(8.dp))
                Text("Join League", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Premium Features Section
        Text(
            "Premium Features", 
            color = Color.White, 
            modifier = Modifier.align(Alignment.Start),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GlassCard(modifier = Modifier.weight(1f), onClick = onCreateLeague) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow)
                Text("UCL League", color = Color.White, fontSize = 12.sp)
            }
            GlassCard(modifier = Modifier.weight(1f), onClick = onCreateLeague) {
                Icon(Icons.Default.List, contentDescription = null, tint = Color.Cyan)
                Text("Group League", color = Color.White, fontSize = 12.sp)
            }
            GlassCard(modifier = Modifier.weight(1f), onClick = onViewLeagues) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                Text("My Leagues", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}
