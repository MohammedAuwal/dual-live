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
import com.duallive.app.ui.home.HomeScreen
import com.duallive.app.ui.components.MainBottomBar
import com.duallive.app.data.AppDatabase
import com.duallive.app.data.entity.*
import com.duallive.app.utils.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)

        setContent {
            var currentScreen by rememberSaveable { mutableStateOf("home") }
            var selectedLeague by remember { mutableStateOf<League?>(null) }
            var showAddTeamDialog by remember { mutableStateOf(false) }
            var winnerName by remember { mutableStateOf<String?>(null) }
            
            var homeTeamForDisplay by remember { mutableStateOf<Team?>(null) }
            var awayTeamForDisplay by remember { mutableStateOf<Team?>(null) }
            var homeScore by remember { mutableStateOf(0) }
            var awayScore by remember { mutableStateOf(0) }
            var generatedFixtures by remember { mutableStateOf<List<Fixture>>(emptyList()) }
            var currentStageLabel by rememberSaveable { mutableStateOf("") }

            BackHandler(enabled = currentScreen != "home") {
                currentScreen = when (currentScreen) {
                    "league_list", "create_league" -> "home"
                    "team_list" -> "league_list"
                    "fixture_list", "match_entry", "match_history", "standings", "knockout_select" -> "team_list"
                    "live_display" -> "fixture_list"
                    else -> "home"
                }
            }

            val leagues by db.leagueDao().getAllLeagues().collectAsState(initial = emptyList())
            val teams by if (selectedLeague != null) {
                db.teamDao().getTeamsByLeague(selectedLeague!!.id).collectAsState(initial = emptyList())
            } else {
                remember { mutableStateOf(emptyList<Team>()) }
            }
            val matches by if (selectedLeague != null) {
                db.matchDao().getMatchesByLeague(selectedLeague!!.id).collectAsState(initial = emptyList())
            } else {
                remember { mutableStateOf(emptyList<Match>()) }
            }
            val standings = remember(teams, matches) { TableCalculator.calculate(teams, matches) }

            MaterialTheme {
                Scaffold(
                    bottomBar = {
                        if (currentScreen == "home" || currentScreen == "league_list") {
                            MainBottomBar(currentScreen = currentScreen, onNavigate = { currentScreen = it })
                        }
                    }
                ) { padding ->
                    Surface(modifier = Modifier.fillMaxSize().padding(padding)) {
                        winnerName?.let { name ->
                            AlertDialog(
                                onDismissRequest = { winnerName = null },
                                title = { Text("🏆 CHAMPION 🏆", fontWeight = FontWeight.Bold) },
                                text = { Text("$name has won the competition!") },
                                confirmButton = {
                                    Button(onClick = { 
                                        winnerName = null
                                        currentStageLabel = ""
                                        currentScreen = "home" 
                                    }) { Text("OK") }
                                }
                            )
                        }

                        when (currentScreen) {
                            "home" -> HomeScreen(
                                onCreateLeague = { currentScreen = "create_league" },
                                onViewLeagues = { currentScreen = "league_list" },
                                onJoinSubmit = { id -> println("Joining League: $id") }
                            )
                            "league_list" -> LeagueListScreen(
                                leagues = leagues, 
                                onLeagueClick = { league -> 
                                    selectedLeague = league
                                    generatedFixtures = emptyList()
                                    currentStageLabel = if (league.name.contains("-")) league.name.substringAfter("- ").trim() else ""
                                    currentScreen = "team_list" 
                                }, 
                                onDeleteLeague = { league -> MainScope().launch { db.leagueDao().deleteLeague(league) } }, 
                                onAddLeagueClick = { currentScreen = "create_league" }
                            )
                            "create_league" -> CreateLeagueScreen(onSave = { name, desc, homeAway, leagueType -> 
                                MainScope().launch { 
                                    db.leagueDao().insertLeague(League(name = name, description = desc, isHomeAndAway = homeAway, type = leagueType))
                                    currentScreen = "league_list" 
                                } 
                            })
                            "team_list" -> {
                                Scaffold(
                                    bottomBar = {
                                        BottomAppBar {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                                TextButton(onClick = { 
                                                    generatedFixtures = if (selectedLeague?.type == LeagueType.CLASSIC) {
                                                        FixtureGenerator.generateRoundRobin(teams, selectedLeague?.isHomeAndAway ?: false)
                                                    } else {
                                                        if (currentStageLabel.isEmpty()) FixtureGenerator.generateRoundRobin(teams, selectedLeague?.isHomeAndAway ?: false)
                                                        else FixtureGenerator.generateKnockoutDraw(teams, currentStageLabel)
                                                    }
                                                    currentScreen = "fixture_list"
                                                }) { Text("DRAW") }
                                                TextButton(onClick = { currentScreen = "match_entry" }) { Text("MANUAL") }
                                                TextButton(onClick = { currentScreen = "match_history" }) { Text("RESULTS") }
                                                TextButton(onClick = { currentScreen = "standings" }) { Text("TABLE") }
                                            }
                                        }
                                    }
                                ) { p ->
                                    Box(modifier = Modifier.padding(p)) {
                                        TeamListScreen(
                                            leagueName = if (currentStageLabel.isEmpty()) selectedLeague?.name ?: "" else "${selectedLeague?.name?.substringBefore(" -")} - $currentStageLabel", 
                                            teams = teams, 
                                            isUcl = selectedLeague?.type == LeagueType.UCL, 
                                            onBack = { currentScreen = "league_list" }, 
                                            onAddTeamClick = { showAddTeamDialog = true }, 
                                            onUpdateTeam = { updatedTeam -> MainScope().launch { db.teamDao().insertTeam(updatedTeam) } }
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
                                onMatchSelect = { h, a -> 
                                    homeTeamForDisplay = h
                                    awayTeamForDisplay = a
                                    homeScore = 0
                                    awayScore = 0
                                    currentScreen = "live_display" 
                                }, 
                                onBack = { currentScreen = "team_list" }
                            )
                            "live_display" -> MatchDisplayScreen(
                                homeName = homeTeamForDisplay?.name ?: "", 
                                awayName = awayTeamForDisplay?.name ?: "", 
                                homeScore = homeScore, 
                                awayScore = awayScore, 
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
                            "standings" -> StandingsScreen(teams = teams, standings = standings)
                            "match_history" -> MatchHistoryScreen(
                                matches = matches, 
                                teams = teams, 
                                onDeleteMatch = { m -> MainScope().launch { db.matchDao().deleteMatch(m) } }, 
                                onUpdateMatch = { updatedMatch -> MainScope().launch { db.matchDao().insertMatch(updatedMatch) } }, 
                                onBack = { currentScreen = "team_list" }
                            )
                            "match_entry" -> MatchEntryScreen(
                                teams = teams, 
                                onBack = { currentScreen = "team_list" }, 
                                onLaunchDisplay = { h, a -> 
                                    homeTeamForDisplay = h; awayTeamForDisplay = a
                                    homeScore = 0; awayScore = 0
                                    currentScreen = "live_display" 
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
