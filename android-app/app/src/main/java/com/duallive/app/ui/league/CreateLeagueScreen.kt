package com.duallive.app.ui.league

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.LeagueType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLeagueScreen(
    onLeagueCreated: (String, String, Boolean, LeagueType) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var isHomeAndAway by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(LeagueType.CLASSIC) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup Tournament") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Create New League", style = MaterialTheme.typography.headlineMedium)
            
            OutlinedTextField(
                value = name, 
                onValueChange = { name = it }, 
                label = { Text("League Name (e.g. Champions League)") }, 
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = desc, 
                onValueChange = { desc = it }, 
                label = { Text("Description") }, 
                modifier = Modifier.fillMaxWidth()
            )

            Text("Tournament Type", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedType == LeagueType.CLASSIC,
                    onClick = { selectedType = LeagueType.CLASSIC },
                    label = { Text("Classic League") }
                )
                FilterChip(
                    selected = selectedType == LeagueType.UCL,
                    onClick = { selectedType = LeagueType.UCL },
                    label = { Text("UCL Version") } // User constraint: Keep classic, add UCL
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Home & Away Format", modifier = Modifier.weight(1f))
                Switch(checked = isHomeAndAway, onCheckedChange = { isHomeAndAway = it })
            }
            
            Button(
                onClick = { onLeagueCreated(name, desc, isHomeAndAway, selectedType) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = name.isNotBlank()
            ) {
                Text("CREATE LEAGUE")
            }
        }
    }
}
