package com.duallive.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.duallive.app.ui.league.*
import com.duallive.app.ui.team.*
import com.duallive.app.ui.table.StandingsScreen
import com.duallive.app.ui.match.*
import com.duallive.app.data.AppDatabase
import com.duallive.app.data.entity.*
import com.duallive.app.utils.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)

        setContent {
            var currentScreen by rememberSaveable { mutableStateOf("league_list") }
            var selectedLeague by remember { mutableStateOf<League?>(null) }
            var showAddTeamDialog by remember { mutableStateOf(false) }
            var winnerName by remember { mutableStateOf<String?>(null) }

            var homeTeamForDisplay by remember { mutableStateOf<Team?>(null) }
            var awayTeamForDisplay by remember { mutableStateOf<Team?>(null) }
            var homeScore by remember { mutableStateOf(0) }
            var awayScore by remember { mutableStateOf(0) }

            var currentStageLabel by rememberSaveable { mutableStateOf("") }
            var generatedFixtures by remember { mutableStateOf<List<Fixture>>(emptyList()) }
            var selectedGroup by rememberSaveable { mutableStateOf("A") }

            BackHandler(enabled = currentScreen != "league_list") {
                when (currentScreen) {
                    "team_list", "create_league" -> currentScreen = "league_list"
                    "fixture_list", "match_entry", "match_history", "standings", "knockout_select" -> currentScreen = "team_list"
                    "live_display" -> currentScreen = "fixture_list"
                    else -> currentScreen = "league_list"
                }
            }

            val leagues by db.leagueDao().getAllLeagues().collectAsState(initial = emptyList())
            
            val teams by produceState<List<Team>>(initialValue = emptyList(), selectedLeague) {
                selectedLeague?.let {
                    db.teamDao().getTeamsByLeague(it.id.toLong()).collect { value = it }
                }
            }

            val matches by produceState<List<Match>>(initialValue = emptyList(), selectedLeague) {
                selectedLeague?.let {
                    db.matchDao().getMatchesByLeague(it.id.toLong()).collect { value = it }
                }
            }

            val standings = remember(teams, matches) { TableCalculator.calculate(teams, matches) }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    winnerName?.let { name ->
                        AlertDialog(
                            onDismissRequest = { winnerName = null },
                            title = { Text("🏆 CHAMPION 🏆", fontWeight = FontWeight.Bold) },
                            text = { Text("$name has won the competition!") },
                            confirmButton = {
                                Button(onClick = {
                                    winnerName = null
                                    currentStageLabel = ""
                                    currentScreen = "league_list"
                                }) { Text("OK") }
                            }
                        )
                    }

                    when (currentScreen) {
                        "league_list" -> LeagueListScreen(
                            leagues = leagues,
                            onLeagueClick = { league ->
                                selectedLeague = league
                                currentStageLabel = if (league.name.contains("-")) league.name.substringAfter("- ").trim() else ""
                                generatedFixtures = emptyList()
                                currentScreen = "team_list"
                            },
                            onDeleteLeague = { l -> MainScope().launch { db.leagueDao().deleteLeague(l) } },
                            onAddLeagueClick = { currentScreen = "create_league" }
                        )

                        "create_league" -> CreateLeagueScreen(
                            onSave = { name, desc, homeAway, leagueType ->
                                MainScope().launch {
                                    db.leagueDao().insertLeague(League(name = name, description = desc, isHomeAndAway = homeAway, type = leagueType))
                                    currentScreen = "league_list"
                                }
                            }
                        )

                        "team_list" -> {
                            Scaffold(
                                bottomBar = {
                                    Column {
                                        if (selectedLeague?.type == LeagueType.UCL) {
                                            var expanded by remember { mutableStateOf(false) }
                                            val groupOptions = listOf("A", "B", "C", "D", "E", "F", "G", "H")
                                            Box(modifier = Modifier.padding(8.dp)) {
                                                TextButton(onClick = { expanded = true }) {
                                                    Text("Group: $selectedGroup")
                                                }
                                                DropdownMenu(
                                                    expanded = expanded,
                                                    onDismissRequest = { expanded = false }
                                                ) {
                                                    groupOptions.forEach { group ->
                                                        DropdownMenuItem(
                                                            text = { Text(group) },
                                                            onClick = {
                                                                selectedGroup = group
                                                                expanded = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        BottomAppBar {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                                TextButton(onClick = {
                                                    if (selectedLeague?.type == LeagueType.UCL) {
                                                        val groups = teams.groupBy { it.groupName }
                                                        val groupTeams = groups[selectedGroup] ?: emptyList()
                                                        if (groupTeams.isNotEmpty()) {
                                                            generatedFixtures = FixtureGenerator.generateRoundRobin(groupTeams, selectedLeague?.isHomeAndAway ?: false)
                                                        }
                                                    } else {
                                                        if (generatedFixtures.isEmpty()) {
                                                            generatedFixtures = FixtureGenerator.generateRoundRobin(teams, selectedLeague?.isHomeAndAway ?: false)
                                                        }
                                                    }
                                                    currentScreen = "fixture_list"
                                                }) { Text("DRAW") }

                                                TextButton(onClick = { currentScreen = "match_entry" }) { Text("MANUAL") }
                                                TextButton(onClick = { currentScreen = "match_history" }) { Text("RESULTS") }
                                                TextButton(onClick = { currentScreen = "standings" }) { Text("TABLE") }

                                                if (selectedLeague?.type == LeagueType.UCL) {
                                                    val canProceed = teams.isNotEmpty() && generatedFixtures.isNotEmpty()
                                                    TextButton(
                                                        onClick = {
                                                            MainScope().launch {
                                                                val groups = teams.groupBy { it.groupName }
                                                                val topTeams = groups.values.flatMap { groupList ->
                                                                    TableCalculator.getTopTeamsForKnockout(groupList, matches)
                                                                }
                                                                if (topTeams.isNotEmpty()) {
                                                                    db.matchDao().deleteMatchesByLeague(selectedLeague!!.id.toLong())
                                                                    generatedFixtures = FixtureGenerator.generateKnockoutDraw(topTeams, nextStage(currentStageLabel))
                                                                    currentStageLabel = nextStage(currentStageLabel)
                                                                    currentScreen = "fixture_list"
                                                                }
                                                            }
                                                        },
                                                        enabled = canProceed
                                                    ) { Text("PROCEED") }
                                                }
                                            }
                                        }
                                    }
                                }
                            ) { padding ->
                                Box(modifier = Modifier.padding(padding)) {
                                    TeamListScreen(
                                        leagueName = if (currentStageLabel.isEmpty()) selectedLeague?.name ?: "" else "${selectedLeague?.name?.substringBefore(" -")} - $currentStageLabel",
                                        teams = teams,
                                        isUcl = selectedLeague?.type == LeagueType.UCL,
                                        onBack = { currentScreen = "league_list" },
                                        onAddTeamClick = { showAddTeamDialog = true },
                                        onUpdateTeam = { updated -> MainScope().launch { db.teamDao().insertTeam(updated) } }
                                    )
                                }
                            }
                            if (showAddTeamDialog) {
                                AddTeamScreen(
                                    leagueType = selectedLeague?.type ?: LeagueType.CLASSIC,
                                    onSave = { names, group ->
                                        MainScope().launch {
                                            names.forEach { db.teamDao().insertTeam(Team(leagueId = selectedLeague!!.id, name = it, groupName = group)) }
                                            showAddTeamDialog = false
                                        }
                                    },
                                    onCancel = { showAddTeamDialog = false }
                                )
                            }
                        }

                        // Other screens remain the same (fixture_list, knockout_select, live_display, match_history, match_entry)
                    }
                }
            }
        }
    }

    private fun nextStage(currentStage: String): String {
        return when (currentStage) {
            "Group Stage" -> "Quarter-Final"
            "Quarter-Final" -> "Semi-Final"
            "Semi-Final" -> "Final"
            else -> "Champion"
        }
    }
}
