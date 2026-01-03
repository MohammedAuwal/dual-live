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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duallive.app.ui.league.*
import com.duallive.app.ui.team.*
import com.duallive.app.ui.table.StandingsScreen
import com.duallive.app.ui.match.*
import com.duallive.app.ui.home.HomeScreen
import com.duallive.app.ui.components.MainBottomBar
import com.duallive.app.data.AppDatabase
import com.duallive.app.data.entity.*
import com.duallive.app.ucl2026.ui.*
import com.duallive.app.ucl2026.viewmodel.Ucl26ViewModel
import com.duallive.app.utils.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)

        setContent {
            var currentScreen by rememberSaveable { mutableStateOf("home") }
            var selectedLeague by remember { mutableStateOf<League?>(null) }
            var activeLeagueType by rememberSaveable { mutableStateOf(LeagueType.CLASSIC) }
            var showAddTeamDialog by remember { mutableStateOf(false) }
            var winnerName by remember { mutableStateOf<String?>(null) }
            
            var homeTeamForDisplay by remember { mutableStateOf<Team?>(null) }
            var awayTeamForDisplay by remember { mutableStateOf<Team?>(null) }
            var homeScore by remember { mutableStateOf(0) }
            var awayScore by remember { mutableStateOf(0) }
            var generatedFixtures by remember { mutableStateOf<List<Fixture>>(emptyList()) }
            var currentStageLabel by rememberSaveable { mutableStateOf("") }

            val ucl26ViewModel: Ucl26ViewModel = viewModel()

            BackHandler(enabled = currentScreen != "home") {
                currentScreen = when (currentScreen) {
                    "ucl26_registration", "ucl26_league", "league_list", "create_league" -> "home"
                    "team_list" -> "league_list"
                    "fixture_list", "match_history", "standings" -> "team_list"
                    "live_display" -> "fixture_list"
                    else -> "home"
                }
            }

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
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0A192F), Color(0xFF040C1A))))) {
                    Scaffold(
                        containerColor = Color.Transparent,
                        bottomBar = {
                            if (currentScreen == "home" || currentScreen == "league_list") {
                                MainBottomBar(currentScreen = currentScreen, onNavigate = { currentScreen = it })
                            }
                        }
                    ) { padding ->
                        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                            when (currentScreen) {
                                "home" -> HomeScreen(
                                    onNavigateToClassic = { 
                                        activeLeagueType = LeagueType.CLASSIC
                                        currentScreen = "league_list" 
                                    },
                                    onNavigateToUCL = { 
                                        activeLeagueType = LeagueType.UCL
                                        currentScreen = "league_list" 
                                    },
                                    onNavigateToNewUCL = { currentScreen = "ucl26_registration" },
                                    onJoinSubmit = { }
                                )

                                "ucl26_registration" -> Ucl26RegistrationScreen(onTeamsConfirmed = { names ->
                                    ucl26ViewModel.initializeTournament(names)
                                    currentScreen = "ucl26_league"
                                })
                                "ucl26_league" -> Ucl26LeagueScreen(1, ucl26ViewModel, { currentScreen = "ucl26_matches" }, { currentScreen = "ucl26_bracket" })
                                "ucl26_matches" -> Ucl26MatchScreen(ucl26ViewModel, { currentScreen = "ucl26_league" })
                                "ucl26_bracket" -> Ucl26BracketScreen(ucl26ViewModel, { currentScreen = "ucl26_league" })

                                "league_list" -> LeagueListScreen(
                                    leagues = filteredLeagues, 
                                    type = activeLeagueType, 
                                    onLeagueClick = { league -> 
                                        selectedLeague = league
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
                                "fixture_list" -> FixtureListScreen(generatedFixtures, matches, { h, a -> 
                                    homeTeamForDisplay = h; awayTeamForDisplay = a
                                    homeScore = 0; awayScore = 0
                                    currentScreen = "live_display" 
                                }, { currentScreen = "team_list" })
                                "live_display" -> MatchDisplayScreen(homeTeamForDisplay?.name ?: "", awayTeamForDisplay?.name ?: "", homeScore, awayScore, { homeScore += it }, { awayScore += it }, {
                                    MainScope().launch {
                                        db.matchDao().insertMatch(Match(leagueId = selectedLeague!!.id, homeTeamId = homeTeamForDisplay!!.id, awayTeamId = awayTeamForDisplay!!.id, homeScore = homeScore, awayScore = awayScore))
                                        currentScreen = "fixture_list"
                                    }
                                }, { currentScreen = "fixture_list" })
                                "standings" -> StandingsScreen(teams, standings)
                                "match_history" -> MatchHistoryScreen(matches, teams, { m -> MainScope().launch { db.matchDao().deleteMatch(m) } }, { m -> MainScope().launch { db.matchDao().insertMatch(m) } }, { currentScreen = "team_list" })
                            }
                        }
                    }
                }
            }
        }
    }
}
