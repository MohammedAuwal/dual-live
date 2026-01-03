package com.duallive.app.ucl2026.ui

// ... (existing imports)

@Composable
fun Ucl26BracketScreen(viewModel: Ucl26ViewModel, onBack: () -> Unit) {
    val bracketMatches by viewModel.bracketMatches.collectAsState()
    val r16Matches by viewModel.r16Matches.collectAsState()
    val standings by viewModel.standings.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // ... (Top 8 section)

        // SECTION: PLAY-OFFS
        item { Text("PLAY-OFF RESULTS", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold) }
        items(bracketMatches) { match -> BracketMatchItem(match.team1Name, match.team2Name, match.aggregate1, match.aggregate2) }

        // SECTION: ROUND OF 16
        if (bracketMatches.isNotEmpty() && r16Matches.isEmpty()) {
            item {
                Button(onClick = { viewModel.generateR16Draw() }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Text("DRAW ROUND OF 16")
                }
            }
        }

        if (r16Matches.isNotEmpty()) {
            item { Text("ROUND OF 16", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 24.dp)) }
            items(r16Matches) { match -> BracketMatchItem(match.team1Name, match.team2Name, match.aggregate1, match.aggregate2) }
        }
    }
}
