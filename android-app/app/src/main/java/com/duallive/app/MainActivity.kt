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

            // Back button handling for better UX
            BackHandler(enabled = currentScreen != "league_list") {
                when (currentScreen) {
                    "team_list", "create_league" -> currentScreen = "league_list"
                    "fixture_list", "match_entry", "match_history", "standings", "knockout_select" -> currentScreen = "team_list"
                    "live_display" -> currentScreen = "fixture_list"
                    else -> currentScreen = "league_list"
                }
            }

            val leagues by db.leagueDao().getAllLeagues().collectAsState(initial = emptyList())
            val teams by if (selectedLeague != null) db.teamDao().getTeamsByLeague(selectedLeague!!.id).collectAsState(initial = emptyList()) else remember { mutableStateOf(emptyList()) }
            val matches by if (selectedLeague != null) db.matchDao().getMatchesByLeague(selectedLeague!!.id).collectAsState(initial = emptyList()) else remember { mutableStateOf(emptyList()) }

            val standings = remember(teams, matches) { TableCalculator.calculate(teams, matches) }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    // Winner Celebration Dialog
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
                                    BottomAppBar {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                            TextButton(onClick = {
                                                if (generatedFixtures.isEmpty()) {
                                                    generatedFixtures = FixtureGenerator.generateRoundRobin(teams, selectedLeague?.isHomeAndAway ?: false)
                                                }
                                                currentScreen = "fixture_list"
                                            }) { Text("DRAW") }

                                            TextButton(onClick = { currentScreen = "match_entry" }) { Text("MANUAL") }
                                            TextButton(onClick = { currentScreen = "match_history" }) { Text("RESULTS") }
                                            TextButton(onClick = { currentScreen = "standings" }) { Text("TABLE") }
                                            
                                            if (selectedLeague?.type == LeagueType.UCL) {
                                                TextButton(onClick = { currentScreen = "knockout_select" }) { Text("PROCEED") }
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

                        "fixture_list" -> FixtureListScreen(
                            fixtures = generatedFixtures,
                            matches = matches,
                            teams = teams,
                            onMatchSelect = { h, a -> 
                                homeTeamForDisplay = h
                                awayTeamForDisplay = a
                                homeScore = 0
                                awayScore = 0
                                currentScreen = "live_display" 
                            },
                            onBack = { currentScreen = "team_list" }
                        )

                        "knockout_select" -> KnockoutSelectionScreen(
                            teams = teams,
                            standings = standings,
                            onBack = { currentScreen = "team_list" },
                            onConfirmKnockouts = { selectedTeams, stage ->
                                if (stage == "Champion") {
                                    winnerName = selectedTeams.firstOrNull()?.name ?: "Unknown"
                                } else {
                                    generatedFixtures = FixtureGenerator.generateKnockoutDraw(selectedTeams, stage)
                                    currentStageLabel = stage
                                    currentScreen = "fixture_list"
                                }
                            }
                        )

                        "standings" -> StandingsScreen(teams = teams, standings = standings)

                        "live_display" -> MatchDisplayScreen(
                            homeName = homeTeamForDisplay?.name ?: "",
                            awayName = awayTeamForDisplay?.name ?: "",
                            homeScore = homeScore,
                            awayScore = awayScore,
                            stageLabel = currentStageLabel,
                            onUpdateHome = { homeScore += it },
                            onUpdateAway = { awayScore += it },
                            onSaveAndClose = {
                                MainScope().launch {
                                    db.matchDao().insertMatch(Match(
                                        leagueId = selectedLeague!!.id,
                                        homeTeamId = homeTeamForDisplay!!.id,
                                        awayTeamId = awayTeamForDisplay!!.id,
                                        homeScore = homeScore,
                                        awayScore = awayScore
                                    ))
                                    currentScreen = "fixture_list"
                                }
                            },
                            onCancel = { currentScreen = "fixture_list" }
                        )

                        "match_history" -> MatchHistoryScreen(
                            matches = matches,
                            teams = teams,
                            onDeleteMatch = { m -> MainScope().launch { db.matchDao().deleteMatch(m) } },
                            onUpdateMatch = { m -> MainScope().launch { db.matchDao().insertMatch(m) } },
                            onBack = { currentScreen = "team_list" }
                        )

                        "match_entry" -> MatchEntryScreen(
                            teams = teams,
                            onBack = { currentScreen = "team_list" },
                            onLaunchDisplay = { h, a ->
                                homeTeamForDisplay = h
                                awayTeamForDisplay = a
                                homeScore = 0
                                awayScore = 0
                                currentScreen = "live_display"
                            }
                        )
                    }
                }
            }
        }
    }
}
