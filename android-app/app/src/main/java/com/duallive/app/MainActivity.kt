package com.duallive.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duallive.app.ui.league.*
import com.duallive.app.ui.team.*
import com.duallive.app.ui.table.StandingsScreen
import com.duallive.app.ui.match.MatchEntryScreen
import com.duallive.app.data.AppDatabase
import com.duallive.app.data.entity.*
import com.duallive.app.utils.TableCalculator
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)

        setContent {
            var currentScreen by remember { mutableStateOf("league_list") }
            var selectedLeague by remember { mutableStateOf<League?>(null) }
            var showAddTeamDialog by remember { mutableStateOf(false) }

            // 1. Fetch Leagues
            val leagues by db.leagueDao().getAllLeagues().collectAsState(initial = emptyList())
            
            // 2. Fetch Teams for selected league
            val teams by if (selectedLeague != null) {
                db.teamDao().getTeamsByLeague(selectedLeague!!.id).collectAsState(initial = emptyList())
            } else {
                remember { mutableStateOf(emptyList<Team>()) }
            }

            // 3. Fetch Matches for selected league
            val matches by if (selectedLeague != null) {
                db.matchDao().getMatchesByLeague(selectedLeague!!.id).collectAsState(initial = emptyList())
            } else {
                remember { mutableStateOf(emptyList<Match>()) }
            }
            
            // 4. AUTOMATIC CALCULATION: Runs whenever teams or matches change
            val standings = remember(teams, matches) {
                TableCalculator.calculate(teams, matches)
            }

            MaterialTheme {
                Surface {
                    when (currentScreen) {
                        "league_list" -> LeagueListScreen(
                            leagues = leagues,
                            onLeagueClick = { league -> 
                                selectedLeague = league
                                currentScreen = "team_list" 
                            },
                            onAddLeagueClick = { currentScreen = "create_league" }
                        )
                        "create_league" -> CreateLeagueScreen(onSave = { name, desc ->
                            MainScope().launch {
                                db.leagueDao().insertLeague(League(name = name, description = desc))
                                currentScreen = "league_list"
                            }
                        })
                        "team_list" -> {
                            Scaffold(
                                bottomBar = {
                                    BottomAppBar {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                            TextButton(onClick = { currentScreen = "match_entry" }) {
                                                Text("RECORD MATCH")
                                            }
                                            TextButton(onClick = { currentScreen = "standings" }) {
                                                Text("VIEW TABLE")
                                            }
                                        }
                                    }
                                }
                            ) { padding ->
                                Box(modifier = Modifier.padding(padding)) {
                                    TeamListScreen(
                                        leagueName = selectedLeague?.name ?: "",
                                        teams = teams,
                                        onBack = { currentScreen = "league_list" },
                                        onAddTeamClick = { showAddTeamDialog = true }
                                    )
                                }
                            }

                            if (showAddTeamDialog) {
                                AddTeamScreen(
                                    onSave = { teamNames ->
                                        MainScope().launch {
                                            teamNames.forEach { name ->
                                                db.teamDao().insertTeam(Team(leagueId = selectedLeague!!.id, name = name))
                                            }
                                            showAddTeamDialog = false
                                        }
                                    },
                                    onCancel = { showAddTeamDialog = false }
                                )
                            }
                        }
                        "match_entry" -> MatchEntryScreen(
                            teams = teams,
                            onBack = { currentScreen = "team_list" },
                            onSaveMatch = { hId, aId, hSc, aSc ->
                                MainScope().launch {
                                    db.matchDao().insertMatch(Match(
                                        leagueId = selectedLeague!!.id, 
                                        homeTeamId = hId, 
                                        awayTeamId = aId, 
                                        homeScore = hSc, 
                                        awayScore = aSc
                                    ))
                                    currentScreen = "standings"
                                }
                            }
                        )
                        "standings" -> {
                            Scaffold(
                                bottomBar = {
                                    Button(onClick = { currentScreen = "team_list" }, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                        Text("Back to Teams")
                                    }
                                }
                            ) { p ->
                                Box(modifier = Modifier.padding(p)) {
                                    StandingsScreen(teams = teams, standings = standings)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
