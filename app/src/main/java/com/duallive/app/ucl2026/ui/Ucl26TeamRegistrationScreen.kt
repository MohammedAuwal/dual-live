package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.ucl2026.model.Ucl26Team

@Composable
fun Ucl26TeamRegistrationScreen(
    availableTeams: List<Ucl26Team>,
    onTeamSelected: (Ucl26Team) -> Unit
) {
    var selectedCount by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
    ) {
        // Header with Counter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("TEAM REGISTRATION", color = GoldAccent, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Select 36 Teams for the Season", color = Color.Gray, fontSize = 12.sp)
            }
            
            // Modern Counter Circle
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(GlassWhite, CircleShape)
                    .border(2.dp, if (selectedCount == 36) Color.Green else GoldAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("$selectedCount", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pot Legend
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PotChip("Pot 1", Color(0xFFE91E63))
            PotChip("Pot 2", Color(0xFF2196F3))
            PotChip("Pot 3", Color(0xFFFF9800))
            PotChip("Pot 4", Color(0xFF9C27B0))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(availableTeams) { team ->
                TeamSelectCard(team) {
                    onTeamSelected(team)
                    selectedCount++
                }
            }
        }
    }
}

@Composable
fun PotChip(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(label, color = color, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
    }
}

@Composable
fun TeamSelectCard(team: Ucl26Team, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassWhite, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(32.dp).background(Color.White.copy(0.1f), CircleShape)) // Logo Placeholder
            Spacer(Modifier.width(12.dp))
            Text(team.name, color = Color.White, fontSize = 16.sp)
        }
        
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = GoldAccent
        )
    }
}
