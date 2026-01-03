package com.duallive.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
        val intentData = intent?.data
        val deepLinkCode = if (intentData?.pathSegments?.contains("join") == true) {
            intentData.lastPathSegment
        } else null

        setContent {
            var currentScreen by rememberSaveable { mutableStateOf("home") }
            var selectedLeague by remember { mutableStateOf<League?>(null) }
            var showAddTeamDialog by remember { mutableStateOf(false) }
            var winnerName by remember { mutableStateOf<String?>(null) }
            
            // State to track which tournament version is active (Classic vs UCL)
            var activeLeagueType by rememberSaveable { mutableStateOf(LeagueType.CLASSIC) }

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

            // Filter leagues dynamically based on the dashboard selection
            val leagues by db.leagueDao().getAllLeagues().collectAsState(initial = emptyList())
            val filteredLeagues = leagues.filter { it.type == activeLeagueType }

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
                // Glassmorphism Navy Background Container
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF0A192F), Color(0xFF040C1A))
                            )
                        )
                ) {
                    Scaffold(
                        containerColor = Color.Transparent, // Transparent to show gradient
                        bottomBar = {
                            if (currentScreen == "home" || currentScreen == "league_list") {
                                MainBottomBar(currentScreen = currentScreen, onNavigate = { currentScreen = it })
                            }
                        }
                    ) { padding ->
                        Surface(
                            modifier = Modifier.fillMaxSize().padding(padding),
                            color = Color.Transparent
                        ) {
                            winnerName?.let { name ->
                                AlertDialog(
                                    onDismissRequest = { winnerName = null },
                                    containerColor = Color(0xFF0A192F),
                                    title = { Text("🏆 CHAMPION 🏆", fontWeight = FontWeight.Bold, color = Color.White) },
                                    text = { Text("$name has won the competition!", color = Color.White.copy(alpha = 0.8f)) },
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
                                    onNavigateToClassic = { 
                                        activeLeagueType = LeagueType.CLASSIC
                                        currentScreen = "league_list" 
                                    },
                                    onNavigateToNewUCL = { }, onNavigateToUCL = { 
                                        activeLeagueType = LeagueType.UCL
                                        currentScreen = "league_list" 
                                    },
                                    onJoinSubmit = { id -> println("Joining League: $id") }
                                )
                                "league_list" -> LeagueListScreen(
                                    leagues = filteredLeagues, 
                                    type = activeLeagueType, 
                                    onLeagueClick = { league -> 
                                        selectedLeague = league
                                        generatedFixtures = emptyList()
                                        currentStageLabel = if (league.name.contains("-")) league.name.substringAfter("- ").trim() else ""
                                        currentScreen = "team_list" 
                                    }, 
                                    onDeleteLeague = { league -> MainScope().launch { db.leagueDao().deleteLeague(league) } }, 
                                    onAddLeagueClick = { currentScreen = "create_league" }
                                )
                                "create_league" -> CreateLeagueScreen(
                                    preselectedType = activeLeagueType,
                                    onSave = { name, desc, homeAway, leagueType -> 
                                        MainScope().launch { 
                                            db.leagueDao().insertLeague(League(name = name, description = desc, isHomeAndAway = homeAway, type = leagueType))
                                            currentScreen = "league_list" 
                                        } 
                                    }
                                )
                                "team_list" -> {
                                    Scaffold(
                                        containerColor = Color.Transparent,
                                        bottomBar = {
                                            BottomAppBar(containerColor = Color.White.copy(alpha = 0.05f)) {
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                                    TextButton(onClick = { 
                                                        generatedFixtures = if (selectedLeague?.type == LeagueType.CLASSIC) {
                                                            FixtureGenerator.generateRoundRobin(teams, selectedLeague?.isHomeAndAway ?: false)
                                                        } else {
                                                            if (currentStageLabel.isEmpty()) FixtureGenerator.generateRoundRobin(teams, selectedLeague?.isHomeAndAway ?: false)
                                                            else FixtureGenerator.generateKnockoutDraw(teams, currentStageLabel)
                                                        }
                                                        currentScreen = "fixture_list"
                                                    }) { Text("DRAW", color = Color.White) }
                                                    TextButton(onClick = { currentScreen = "match_history" }) { Text("RESULTS", color = Color.White) }
                                                    TextButton(onClick = { currentScreen = "standings" }) { Text("TABLE", color = Color.White) }
                                                }
                                            }
                                        }
                                    ) { p ->
                                        Box(modifier = Modifier.padding(p)) {
                                            TeamListScreen(
                                                leagueName = if (currentStageLabel.isEmpty()) selectedLeague?.name ?: "" else "${selectedLeague?.name?.substringBefore(" -")} - $currentStageLabel",
                                                inviteCode = selectedLeague?.inviteCode ?: "DL-0000", 
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
}
