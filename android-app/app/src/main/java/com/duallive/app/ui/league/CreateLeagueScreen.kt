package com.duallive.app.ui.league

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateLeagueScreen(onSave: (String, String, Boolean) -> Unit) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var isHomeAndAway by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Create New League", style = MaterialTheme.typography.headlineMedium)
        
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("League Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())

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
            onClick = { onSave(name, desc, isHomeAndAway) },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank()
        ) {
            Text("Create League")
        }
    }
}
