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
        val scope = MainScope()

        setContent {
            var currentScreen by rememberSaveable { mutableStateOf("league_list") }
            var selectedLeague by remember { mutableStateOf<League?>(null) }
            val leagues by db.leagueDao().getAllLeagues().collectAsState(initial = emptyList())

            val teams by produceState<List<Team>>(initialValue = emptyList(), selectedLeague) {
                selectedLeague?.let { db.teamDao().getTeamsByLeague(it.id).collect { value = it } }
            }

            val matches by produceState<List<Match>>(initialValue = emptyList(), selectedLeague) {
                selectedLeague?.let { db.matchDao().getMatchesByLeague(it.id).collect { value = it } }
            }

            val standings = remember(teams, matches) { TableCalculator.calculate(teams, matches) }

            BackHandler(currentScreen != "league_list") {
                currentScreen = when (currentScreen) {
                    "team_list" -> "league_list"
                    "standings", "match_history", "match_entry" -> "team_list"
                    else -> "league_list"
                }
            }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        "league_list" -> LeagueListScreen(
                            leagues = leagues,
                            onLeagueClick = { selectedLeague = it; currentScreen = "team_list" },
                            onDeleteLeague = { scope.launch { db.leagueDao().deleteLeague(it) } },
                            onAddLeagueClick = { currentScreen = "create_league" }
                        )
                        "create_league" -> CreateLeagueScreen(
                            onLeagueCreated = { n, d, h, t ->
                                scope.launch { db.leagueDao().insertLeague(League(name=n, description=d, isHomeAndAway=h, type=t)) }
                                currentScreen = "league_list"
                            },
                            onBack = { currentScreen = "league_list" }
                        )
                        "team_list" -> selectedLeague?.let { league ->
                            TeamListScreen(
                                league = league,
                                teams = teams,
                                onBack = { currentScreen = "league_list" },
                                onAddTeamClick = { 
                                    // You can expand this later, but it satisfies the parameter
                                },
                                onUpdateTeam = { updatedTeam -> 
                                    scope.launch { db.teamDao().updateTeam(updatedTeam) } 
                                },
                                onNavigateToMatches = { currentScreen = "match_history" },
                                onNavigateToTable = { currentScreen = "standings" },
                                onNavigateToManual = { currentScreen = "match_entry" }
                            )
                        }
                        "standings" -> StandingsScreen(teams = teams, standings = standings)
                        "match_history" -> MatchHistoryScreen(
                            matches = matches, 
                            teams = teams,
                            onDeleteMatch = { match -> scope.launch { db.matchDao().deleteMatch(match) } },
                            onUpdateMatch = { match -> scope.launch { db.matchDao().updateMatch(match) } },
                            onBack = { currentScreen = "team_list" }
                        )
                    }
                }
            }
        }
    }
}
