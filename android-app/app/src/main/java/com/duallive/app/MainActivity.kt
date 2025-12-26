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

            val leagues by db.leagueDao().getAllLeagues().collectAsState(initial = emptyList())
            
            val teams by if (selectedLeague != null) {
                db.teamDao().getTeamsByLeague(selectedLeague!!.id).collectAsState(initial = emptyList())
            } else {
                remember { mutableStateOf(emptyList<Team>()) }
            }
            
            val standings = teams.map { Standing(teamId = it.id, points = 0) }

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
                            Box {
                                Column {
                                    TeamListScreen(
                                        leagueName = selectedLeague?.name ?: "",
                                        teams = teams,
                                        onBack = { currentScreen = "league_list" },
                                        onAddTeamClick = { showAddTeamDialog = true }
                                    )
                                }
                                
                                // Floating navigation buttons for MVP testing
                                Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Bottom) {
                                    Button(onClick = { currentScreen = "match_entry" }, modifier = Modifier.fillMaxWidth()) {
                                        Text("Record Match")
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(onClick = { currentScreen = "standings" }, modifier = Modifier.fillMaxWidth()) {
                                        Text("View Table")
                                    }
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
                                    db.matchDao().insertMatch(Match(leagueId = selectedLeague!!.id, homeTeamId = hId, awayTeamId = aId, homeScore = hSc, awayScore = aSc))
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
