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
import androidx.compose.ui.unit.sp
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
            var currentStageLabel by remember { mutableStateOf("") }

            BackHandler(enabled = currentScreen != "league_list") {
                when (currentScreen) {
                    "team_list", "create_league" -> currentScreen = "league_list"
                    "fixture_list", "match_entry", "match_history", "standings", "knockout_select" -> currentScreen = "team_list"
                    "live_display" -> currentScreen = "fixture_list"
                    else -> currentScreen = "league_list"
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
            
            val standings = remember(teams, matches) {
                TableCalculator.calculate(teams, matches)
            }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // --- CHAMPION DIALOG ---
                    winnerName?.let { name ->
                        AlertDialog(
                            onDismissRequest = { winnerName = null },
                            title = { Text("🏆 CHAMPIONS LEAGUE WINNER 🏆", fontWeight = FontWeight.Bold) },
                            text = { 
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Text(name, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Congratulations! You are the champion.")
                                }
                            },
                            confirmButton = {
                                Button(onClick = { 
                                    winnerName = null
                                    currentScreen = "league_list" 
                                }) { Text("Great!") }
                            }
                        )
                    }

                    when (currentScreen) {
                        "league_list" -> LeagueListScreen(
                            leagues = leagues,
                            onLeagueClick = { league -> 
                                selectedLeague = league
                                generatedFixtures = emptyList()
                                currentScreen = "team_list" 
                            },
                            onDeleteLeague = { league -> MainScope().launch { db.leagueDao().deleteLeague(league) } },
                            onAddLeagueClick = { currentScreen = "create_league" }
                        )
                        "create_league" -> CreateLeagueScreen(onSave = { name, desc, homeAway, leagueType ->
                            MainScope().launch {
                                db.leagueDao().insertLeague(
                                    League(name = name, description = desc, isHomeAndAway = homeAway, type = leagueType)
                                )
                                currentScreen = "league_list"
                            }
                        })
                        "team_list" -> {
                            Scaffold(
                                bottomBar = {
                                    BottomAppBar {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                            TextButton(onClick = { 
                                                generatedFixtures = FixtureGenerator.generateRoundRobin(teams, selectedLeague?.isHomeAndAway ?: false)
                                                currentStageLabel = ""
                                                currentScreen = "fixture_list"
                                            }) { Text("DRAW") }
                                            
                                            TextButton(onClick = { currentScreen = "match_entry" }) { Text("MANUAL") }
                                            TextButton(onClick = { currentScreen = "match_history" }) { Text("RESULTS") }
                                            TextButton(onClick = { currentScreen = "standings" }) { Text("TABLE") }
                                        }
                                    }
                                }
                            ) { padding ->
                                Box(modifier = Modifier.padding(padding)) {
                                    TeamListScreen(
                                        leagueName = selectedLeague?.name ?: "", 
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
                                            names.forEach { 
                                                db.teamDao().insertTeam(Team(leagueId = selectedLeague!!.id, name = it, groupName = group)) 
                                            }
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
                        "match_entry" -> MatchEntryScreen(
                            teams = teams, 
                            onBack = { currentScreen = "team_list" },
                            onLaunchDisplay = { h, a -> 
                                homeTeamForDisplay = h
                                awayTeamForDisplay = a
                                homeScore = 0
                                awayScore = 0
                                currentStageLabel = ""
                                currentScreen = "live_display" 
                            }
                        )
                        "match_history" -> MatchHistoryScreen(
                            matches = matches,
                            teams = teams,
                            onDeleteMatch = { match -> MainScope().launch { db.matchDao().deleteMatch(match) } }, 
                            onUpdateMatch = { updatedMatch -> MainScope().launch { db.matchDao().insertMatch(updatedMatch) } },
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
                                    db.matchDao().insertMatch(Match(leagueId = selectedLeague!!.id, homeTeamId = homeTeamForDisplay!!.id, awayTeamId = awayTeamForDisplay!!.id, homeScore = homeScore, awayScore = awayScore))
                                    
                                    // CHECK IF IT IS THE FINAL
                                    if (currentStageLabel.contains("Final", ignoreCase = true)) {
                                        winnerName = if (homeScore > awayScore) homeTeamForDisplay?.name else awayTeamForDisplay?.name
                                    } else {
                                        currentScreen = "match_history"
                                    }
                                }
                            },
                            onCancel = { currentScreen = "team_list" },
                            stageLabel = currentStageLabel
                        )
                        "standings" -> {
                            Scaffold(
                                bottomBar = { 
                                    Column {
                                        if (selectedLeague?.type == LeagueType.UCL || currentStageLabel.isNotEmpty()) {
                                            val buttonText = when {
                                                teams.size == 8 && currentStageLabel.contains("Quarter") -> "Proceed to Semi-Finals"
                                                teams.size == 4 && currentStageLabel.contains("Semi") -> "Proceed to Final"
                                                else -> "Proceed to Knockouts"
                                            }
                                            
                                            Button(
                                                onClick = { currentScreen = "knockout_select" },
                                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                            ) { Text(buttonText) }
                                        }
                                        Button(onClick = { currentScreen = "team_list" }, modifier = Modifier.fillMaxWidth().padding(16.dp)) { Text("Back") }
                                    }
                                }
                            ) { p ->
                                Box(modifier = Modifier.padding(p)) { StandingsScreen(teams = teams, standings = standings) }
                            }
                        }
                        "knockout_select" -> {
                            val winningTeams = remember(matches, teams) {
                                if (currentStageLabel.isEmpty()) teams 
                                else {
                                    val winners = mutableListOf<Team>()
                                    matches.forEach { m ->
                                        if (m.homeScore > m.awayScore) {
                                            teams.find { it.id == m.homeTeamId }?.let { winners.add(it) }
                                        } else if (m.awayScore > m.homeScore) {
                                            teams.find { it.id == m.awayTeamId }?.let { winners.add(it) }
                                        }
                                    }
                                    if (winners.isEmpty()) teams else winners
                                }
                            }

                            KnockoutSelectionScreen(
                                teams = winningTeams,
                                standings = standings,
                                onBack = { currentScreen = "standings" },
                                onConfirmKnockouts = { qualifiedTeams, stageLabel ->
                                    MainScope().launch {
                                        val baseName = selectedLeague?.name?.substringBefore(" -") ?: ""
                                        val kName = "$baseName - $stageLabel"
                                        db.leagueDao().insertLeague(
                                            League(name = kName, description = "UCL Stage", isHomeAndAway = false, type = LeagueType.CLASSIC)
                                        )
                                        val allLeagues = db.leagueDao().getAllLeagues().first()
                                        val newLeague = allLeagues.find { it.name == kName }
                                        newLeague?.let { league ->
                                            qualifiedTeams.forEach { team ->
                                                db.teamDao().insertTeam(Team(leagueId = league.id, name = team.name, groupName = null))
                                            }
                                            val newTeams = db.teamDao().getTeamsByLeague(league.id).first()
                                            generatedFixtures = FixtureGenerator.generateKnockoutDraw(newTeams, stageLabel)
                                            currentStageLabel = stageLabel
                                            selectedLeague = league
                                        }
                                        currentScreen = "fixture_list"
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
