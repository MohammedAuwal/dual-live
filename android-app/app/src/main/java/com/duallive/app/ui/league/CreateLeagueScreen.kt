package com.duallive.app.ui.league

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duallive.app.data.entity.LeagueType

@Composable
fun CreateLeagueScreen(onSave: (String, String, Boolean, LeagueType) -> Unit) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var isHomeAndAway by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(LeagueType.CLASSIC) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Create New League", style = MaterialTheme.typography.headlineMedium)
        
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("League Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())

        // --- NEW: League Type Selection ---
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
                label = { Text("UCL (Groups)") }
            )
        }
        // ----------------------------------

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Home & Away Format", modifier = Modifier.weight(1f))
            Switch(checked = isHomeAndAway, onCheckedChange = { isHomeAndAway = it })
        }
        
        Text(
            text = if (isHomeAndAway) "Each team plays twice (e.g. 38 games for 20 teams)" 
                   else "Each team plays once (e.g. 19 games for 20 teams)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Button(
            onClick = { onSave(name, desc, isHomeAndAway, selectedType) },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank()
        ) {
            Text("Create League")
        }
    }
}
