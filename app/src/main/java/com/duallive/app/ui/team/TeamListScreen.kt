package com.duallive.app.ui.team

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.data.entity.Team
import com.duallive.app.ui.components.GlassCard
import com.duallive.app.ui.components.ShareLeagueDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamListScreen(
    leagueName: String,
    inviteCode: String = "DL-2025",
    teams: List<Team>,
    isUcl: Boolean,
    onBack: () -> Unit,
    onAddTeamClick: () -> Unit,
    onUpdateTeam: (Team) -> Unit
) {
    var teamToEdit by remember { mutableStateOf<Team?>(null) }
    var showShareDialog by remember { mutableStateOf(false) }
    
    val accentColor = if (isUcl) Color(0xFFE3BC63) else Color.White

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Custom Glass-Style Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = accentColor)
                }
                Text(
                    text = leagueName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = { showShareDialog = true }) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = accentColor)
            }
        }

        Text(
            text = "${teams.size} Teams Registered",
            color = accentColor.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
        )

        if (teams.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                GlassCard(
                    modifier = Modifier.clickable { onAddTeamClick },
                    tintColor = accentColor
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = accentColor, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Add Your First Teams", color = Color.White)
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(teams) { team ->
                    GlassCard(
                        tintColor = accentColor,
                        modifier = Modifier.height(110.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            IconButton(
                                onClick = { teamToEdit = team },
                                modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = accentColor.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                            }
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = team.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                if (team.groupName != null) {
                                    Text(
                                        text = team.groupName!!,
                                        color = accentColor.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    GlassCard(
                        modifier = Modifier.height(110.dp).clickable { onAddTeamClick },
                        tintColor = accentColor.copy(alpha = 0.3f)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("+ Add More", color = accentColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showShareDialog) {
        ShareLeagueDialog(leagueName = leagueName, inviteCode = inviteCode, onDismiss = { showShareDialog = false })
    }

    if (teamToEdit != null) {
        var editName by remember { mutableStateOf(teamToEdit!!.name) }
        var editGroup by remember { mutableStateOf(teamToEdit!!.groupName ?: "") }

        AlertDialog(
            onDismissRequest = { teamToEdit = null },
            containerColor = Color(0xFF0A192F),
            title = { Text("Edit Team", color = accentColor) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Team Name", color = Color.White.copy(alpha = 0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    if (isUcl) {
                        OutlinedTextField(
                            value = editGroup,
                            onValueChange = { editGroup = it },
                            label = { Text("Group Name", color = Color.White.copy(alpha = 0.6f)) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdateTeam(teamToEdit!!.copy(name = editName, groupName = editGroup.ifBlank { null }))
                    teamToEdit = null
                }) { Text("SAVE", color = accentColor) }
            },
            dismissButton = {
                TextButton(onClick = { teamToEdit = null }) { Text("CANCEL", color = Color.White) }
            }
        )
    }
}
