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
import kotlinx.coroutines.flow.first

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
            
            var generatedFixtures by remember { mutableStateOf<List<Fixture>>(emptyList()) }
            var currentStageLabel by rememberSaveable { mutableStateOf("") }

            val teams by if (selectedLeague != null) db.teamDao().getTeamsByLeague(selectedLeague!!.id).collectAsState(initial = emptyList()) else remember { mutableStateOf(emptyList<Team>()) }
            val matches by if (selectedLeague != null) db.matchDao().getMatchesByLeague(selectedLeague!!.id).collectAsState(initial = emptyList()) else remember { mutableStateOf(emptyList<Match>()) }

            BackHandler(enabled = currentScreen != "league_list") {
                when (currentScreen) {
                    "team_list", "create_league" -> currentScreen = "league_list"
                    "fixture_list", "match_history", "standings", "knockout_select" -> currentScreen = "team_list"
                    "live_display" -> currentScreen = "fixture_list"
                    else -> currentScreen = "league_list"
                }
            }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
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
                            leagues = db.leagueDao().getAllLeagues().collectAsState(initial = emptyList()).value, 
                            onLeagueClick = { league -> 
                                selectedLeague = league
                                generatedFixtures = emptyList()
                                currentStageLabel = if (league.name.contains("-")) league.name.substringAfter("- ").trim() else ""
                                currentScreen = "team_list" 
                            }, 
                            onDeleteLeague = { l -> MainScope().launch { db.leagueDao().deleteLeague(l) } }, 
                            onAddLeagueClick = { currentScreen = "create_league" }
                        )
                        "team_list" -> {
                            Scaffold(
                                bottomBar = {
                                    BottomAppBar {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                            TextButton(onClick = { 
                                                if (generatedFixtures.isEmpty()) {
                                                    generatedFixtures = if (selectedLeague?.type == LeagueType.CLASSIC) {
                                                        FixtureGenerator.generateRoundRobin(teams, selectedLeague?.isHomeAndAway ?: false)
                                                    } else {
                                                        if (currentStageLabel.isEmpty()) FixtureGenerator.generateRoundRobin(teams, selectedLeague?.isHomeAndAway ?: false)
                                                        else FixtureGenerator.generateKnockoutDraw(teams, currentStageLabel)
                                                    }
                                                }
                                                currentScreen = "fixture_list"
                                            }) { Text("DRAW") }
                                            TextButton(onClick = { currentScreen = "match_history" }) { Text("RESULTS") }
                                            TextButton(onClick = { currentScreen = "standings" }) { Text("TABLE") }
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
                                        onUpdateTeam = { updatedTeam -> MainScope().launch { db.teamDao().insertTeam(updatedTeam) } }
                                    )
                                }
                            }
                            if (showAddTeamDialog) {
                                AddTeamScreen(selectedLeague?.type ?: LeagueType.CLASSIC, { names, group -> 
                                    MainScope().launch { 
                                        names.forEach { db.teamDao().insertTeam(Team(leagueId = selectedLeague!!.id, name = it, groupName = group)) }
                                        showAddTeamDialog = false 
                                    } 
                                }, { showAddTeamDialog = false })
                            }
                        }
                        "fixture_list" -> FixtureListScreen(
                            fixtures = generatedFixtures, 
                            matches = matches, 
                            teams = teams,
                            onMatchSelect = { h, a -> homeTeamForDisplay = h; awayTeamForDisplay = a; homeScore = 0; awayScore = 0; currentScreen = "live_display" }, 
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
                        "standings" -> {
                            Scaffold(
                                bottomBar = {
                                    Column {
                                        if (selectedLeague?.type == LeagueType.UCL) {
                                            Button(onClick = { currentScreen = "knockout_select" }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) { Text("Next Round") }
                                        }
                                        Button(onClick = { currentScreen = "team_list" }, modifier = Modifier.fillMaxWidth().padding(16.dp)) { Text("Back") }
                                    }
                                }
                            ) { p -> Box(modifier = Modifier.padding(p)) { StandingsScreen(teams, TableCalculator.calculate(teams, matches)) } }
                        }
                        "match_history" -> MatchHistoryScreen(
                            matches = matches, 
                            teams = teams, 
                            onDeleteMatch = { m -> MainScope().launch { db.matchDao().deleteMatch(m) } }, 
                            onUpdateMatch = { m -> MainScope().launch { db.matchDao().insertMatch(m) } }, 
                            onBack = { currentScreen = "team_list" }
                        )
                        "knockout_select" -> KnockoutSelectionScreen(teams, TableCalculator.calculate(teams, matches), { currentScreen = "standings" }, { qTeams, stage ->
                            MainScope().launch {
                                val baseName = selectedLeague?.name?.substringBefore(" -") ?: ""
                                val kName = "$baseName - $stage"
                                db.leagueDao().insertLeague(League(name = kName, description = "UCL Stage", type = LeagueType.UCL))
                                val newLeague = db.leagueDao().getAllLeagues().first().find { it.name == kName }
                                newLeague?.let { league ->
                                    qTeams.forEach { team -> db.teamDao().insertTeam(Team(leagueId = league.id, name = team.name, groupName = null)) }
                                    selectedLeague = league
                                    currentStageLabel = stage
                                    generatedFixtures = emptyList()
                                }
                                currentScreen = "team_list"
                            }
                        })
                    }
                }
            }
        }
    }
}
