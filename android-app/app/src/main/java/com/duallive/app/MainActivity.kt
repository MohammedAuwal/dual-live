package com.duallive.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.duallive.app.ui.league.*
import com.duallive.app.ui.team.*
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
            
            // Only fetch teams if a league is selected
            val teams by if (selectedLeague != null) {
                db.teamDao().getTeamsByLeague(selectedLeague!!.id).collectAsState(initial = emptyList())
            } else {
                remember { mutableStateOf(emptyList<Team>()) }
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
                            TeamListScreen(
                                leagueName = selectedLeague?.name ?: "Teams",
                                teams = teams,
                                onBack = { currentScreen = "league_list" },
                                onAddTeamClick = { showAddTeamDialog = true }
                            )
                            if (showAddTeamDialog) {
                                AddTeamScreen(
                                    onSave = { teamName ->
                                        MainScope().launch {
                                            db.teamDao().insertTeam(Team(leagueId = selectedLeague!!.id, name = teamName))
                                            showAddTeamDialog = false
                                        }
                                    },
                                    onCancel = { showAddTeamDialog = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
