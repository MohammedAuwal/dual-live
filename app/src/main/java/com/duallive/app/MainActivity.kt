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
            
            // UCL 2026 ViewModel
            val ucl26ViewModel: Ucl26ViewModel = viewModel()

            BackHandler(enabled = currentScreen != "home") {
                currentScreen = when (currentScreen) {
                    "ucl26_registration" -> "home"
                    "ucl26_league", "ucl26_matches", "ucl26_bracket" -> "home"
                    "league_list", "create_league" -> "home"
                    else -> "home"
                }
            }

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
                                    onNavigateToNewUCL = { 
                                        currentScreen = "ucl26_registration" 
                                    },
                                    onJoinSubmit = { }
                                )

                                // NEW UCL 2026 FLOW
                                "ucl26_registration" -> Ucl26RegistrationScreen(onTeamsConfirmed = { teams ->
                                    ucl26ViewModel.initializeTournament(teams)
                                    currentScreen = "ucl26_league"
                                })

                                "ucl26_league" -> Ucl26LeagueScreen(
                                    leagueId = 1,
                                    onNavigateToMatches = { currentScreen = "ucl26_matches" },
                                    onNavigateToBracket = { currentScreen = "ucl26_bracket" },
                                    viewModel = ucl26ViewModel
                                )

                                "ucl26_matches" -> Ucl26MatchScreen(
                                    onBack = { currentScreen = "ucl26_league" },
                                    viewModel = ucl26ViewModel
                                )

                                "ucl26_bracket" -> Ucl26BracketScreen(
                                    onBack = { currentScreen = "ucl26_league" },
                                    viewModel = ucl26ViewModel
                                )

                                // ... rest of your existing league logic ...
                                "league_list" -> { /* Existing League List Code */ }
                            }
                        }
                    }
                }
            }
        }
    }
}
