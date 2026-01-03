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
            
            var currentStageLabel by rememberSaveable { mutableStateOf("") }

            val ucl26ViewModel: Ucl26ViewModel = viewModel()

            BackHandler(enabled = currentScreen != "home") {
                currentScreen = when (currentScreen) {
                    "ucl26_registration", "ucl26_league", "league_list", "create_league" -> "home"
                    "team_list" -> "league_list"
                    "ucl26_matches", "ucl26_bracket" -> "ucl26_league"
                    "standings", "match_history" -> "team_list"
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
                                    onNavigateToClassic = { activeLeagueType = LeagueType.CLASSIC; currentScreen = "league_list" },
                                    onNavigateToUCL = { activeLeagueType = LeagueType.UCL; currentScreen = "league_list" },
                                    onNavigateToNewUCL = { activeLeagueType = LeagueType.SWISS; currentScreen = "league_list" },
                                    onJoinSubmit = { }
                                )

                                "league_list" -> LeagueListScreen(
                                    leagues = filteredLeagues, 
                                    type = activeLeagueType, 
                                    onLeagueClick = { league -> 
                                        selectedLeague = league
                                        if (activeLeagueType == LeagueType.SWISS) {
                                            MainScope().launch {
                                                val teamCount = db.teamDao().getTeamCountForLeague(league.id)
                                                currentScreen = if (teamCount < 36) "ucl26_registration" else "ucl26_league"
                                            }
                                        } else {
                                            currentStageLabel = if (league.name.contains("-")) league.name.substringAfter("- ").trim() else ""
                                            currentScreen = "team_list" 
                                        }
                                    }, 
                                    onDeleteLeague = { league -> MainScope().launch { db.leagueDao().deleteLeague(league) } }, 
                                    onAddLeagueClick = { currentScreen = "create_league" }
                                )

                                "create_league" -> CreateLeagueScreen(
                                    preselectedType = activeLeagueType,
                                    onSave = { name, desc, homeAway, leagueType -> 
                                        MainScope().launch { 
                                            db.leagueDao().insertLeague(League(
                                                name = name, 
                                                description = desc, 
                                                isHomeAndAway = homeAway, 
                                                type = leagueType,
                                                inviteCode = "DL-${(1000..9999).random()}"
                                            ))
                                            currentScreen = "league_list" 
                                        } 
                                    },
                                    onBack = { currentScreen = "league_list" }
                                )

                                "ucl26_registration" -> Ucl26RegistrationScreen(onTeamsConfirmed = { names ->
                                    MainScope().launch {
                                        selectedLeague?.let { league ->
                                            names.forEach { db.teamDao().insertTeam(Team(leagueId = league.id, name = it)) }
                                            ucl26ViewModel.initializeTournament(names)
                                            currentScreen = "ucl26_league"
                                        }
                                    }
                                })

                                "ucl26_league" -> Ucl26LeagueScreen(
                                    leagueId = selectedLeague?.id ?: 0, 
                                    viewModel = ucl26ViewModel, 
                                    onNavigateToMatches = { currentScreen = "ucl26_matches" }, 
                                    onNavigateToBracket = { currentScreen = "ucl26_bracket" }
                                )

                                "ucl26_matches" -> Ucl26MatchScreen(viewModel = ucl26ViewModel, onBack = { currentScreen = "ucl26_league" })
                                
                                "ucl26_bracket" -> Ucl26BracketScreen(viewModel = ucl26ViewModel, onBack = { currentScreen = "ucl26_league" })

                                "team_list" -> TeamListScreen(
                                    leagueName = selectedLeague?.name ?: "",
                                    inviteCode = selectedLeague?.inviteCode ?: "DL-0000",
                                    teams = teams,
                                    isUcl = selectedLeague?.type == LeagueType.UCL,
                                    onBack = { currentScreen = "league_list" },
                                    onAddTeamClick = { showAddTeamDialog = true },
                                    onUpdateTeam = { updatedTeam -> MainScope().launch { db.teamDao().insertTeam(updatedTeam) } }
                                )
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
