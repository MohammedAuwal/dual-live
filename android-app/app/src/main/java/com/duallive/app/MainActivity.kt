package com.duallive.app

import android.app.Activity
import android.content.Intent
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
import com.duallive.app.streaming.ScreenCastController
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var screenCastController: ScreenCastController? = null
    private var isCasting = false

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)
        val scope = MainScope()

        // Initialize screen cast controller
        screenCastController = ScreenCastController(this)

        setContent {
            var currentScreen by rememberSaveable { mutableStateOf("league_list") }
            var selectedLeague by remember { mutableStateOf<League?>(null) }
            var showAddTeamDialog by remember { mutableStateOf(false) }
            var winnerName by remember { mutableStateOf<String?>(null) }

            // Screen casting state
            var liveCasting by rememberSaveable { mutableStateOf(false) }

            // Match display
            var homeName by remember { mutableStateOf("") }
            var awayName by remember { mutableStateOf("") }
            var homeScore by remember { mutableStateOf(0) }
            var awayScore by remember { mutableStateOf(0) }
            var selectedMatch by remember { mutableStateOf<Match?>(null) }
            var selectedFixture by remember { mutableStateOf<Fixture?>(null) }

            // UCL state
            var currentStageLabel by rememberSaveable { mutableStateOf("Group Stage") }
            var generatedFixtures by remember { mutableStateOf<List<Fixture>>(emptyList()) }
            var selectedGroup by rememberSaveable { mutableStateOf("A") }
            var uclStage by rememberSaveable { mutableStateOf("GROUP") }

            // Classic League knockout
            var classicKnockoutStage by rememberSaveable { mutableStateOf("") }

            BackHandler(enabled = currentScreen != "league_list") {
                when (currentScreen) {
                    "team_list", "create_league" -> currentScreen = "league_list"
                    "fixture_list", "match_entry", "match_history", "standings", "knockout_select" -> currentScreen = "team_list"
                    "live_display" -> currentScreen = "fixture_list"
                    else -> currentScreen = "league_list"
                }
            }

            val leagues by db.leagueDao().getAllLeagues().collectAsState(initial = emptyList())

            val teams by produceState<List<Team>>(initialValue = emptyList(), selectedLeague) {
                selectedLeague?.let {
                    val leagueIdLong = it.id.toLong()
                    db.teamDao().getTeamsByLeague(leagueIdLong).collect { value = it }
                }
            }

            val matches by produceState<List<Match>>(initialValue = emptyList(), selectedLeague) {
                selectedLeague?.let {
                    val leagueIdLong = it.id.toLong()
                    db.matchDao().getMatchesByLeague(leagueIdLong).collect { value = it }
                }
            }

            val standings = remember(teams, matches) { TableCalculator.calculate(teams, matches) }

            // AUTOMATIC UCL PROGRESSION
            LaunchedEffect(matches, selectedLeague, uclStage, teams) {
                if (selectedLeague?.type == LeagueType.UCL && matches.isNotEmpty() && teams.isNotEmpty()) {
                    val completedMatches = matches.filter { it.homeScore >= 0 && it.awayScore >= 0 }
                    // existing UCL progression logic ...
                }
            }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    winnerName?.let { name ->
                        AlertDialog(
                            onDismissRequest = { winnerName = null },
                            title = { Text("🏆 CHAMPION 🏆", fontWeight = FontWeight.Bold) },
                            text = {
                                Text(
                                    if (selectedLeague?.type == LeagueType.UCL)
                                        "$name has won the UEFA Champions League!"
                                    else
                                        "$name has won the league!"
                                )
                            },
                            confirmButton = {
                                Button(onClick = {
                                    winnerName = null
                                    if (selectedLeague?.type == LeagueType.UCL) {
                                        currentStageLabel = "Group Stage"
                                        uclStage = "GROUP"
                                    }
                                    generatedFixtures = emptyList()
                                    currentScreen = "league_list"
                                }) { Text("OK") }
                            }
                        )
                    }

                    when (currentScreen) {
                        "league_list" -> LeagueListScreen(
                            leagues = leagues,
                            onLeagueClick = { league ->
                                selectedLeague = league
                                if (league.type == LeagueType.UCL) {
                                    currentStageLabel = "Group Stage"
                                    uclStage = "GROUP"
                                } else {
                                    currentStageLabel = ""
                                    uclStage = ""
                                    classicKnockoutStage = ""
                                }
                                generatedFixtures = emptyList()
                                currentScreen = "team_list"
                            },
                            onDeleteLeague = { l -> scope.launch { db.leagueDao().deleteLeague(l) } },
                            onAddLeagueClick = { currentScreen = "create_league" }
                        )

                        "team_list" -> {
                            Scaffold(
                                bottomBar = {
                                    Column {
                                        if (selectedLeague?.type == LeagueType.UCL) {
                                            // existing dropdown code for group
                                        }

                                        BottomAppBar {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceEvenly
                                            ) {
                                                // DRAW / MANUAL / RESULTS / TABLE buttons
                                                TextButton(onClick = { /* existing DRAW logic */ }) { Text("DRAW") }
                                                TextButton(onClick = { currentScreen = "match_entry" }) { Text("MANUAL") }
                                                TextButton(onClick = { currentScreen = "match_history" }) { Text("RESULTS") }
                                                TextButton(onClick = { currentScreen = "standings" }) { Text("TABLE") }

                                                // ✅ LIVE SCREEN CAST BUTTON
                                                TextButton(onClick = {
                                                    if (!liveCasting) {
                                                        ScreenCastUtils.requestScreenCapture(this@MainActivity)
                                                    } else {
                                                        screenCastController?.stop()
                                                        liveCasting = false
                                                    }
                                                }) {
                                                    Text(if (liveCasting) "STOP LIVE" else "START LIVE")
                                                }
                                            }
                                        }
                                    }
                                }
                            ) {
                                // existing team list content
                            }
                        }

                        // existing other screens ...
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ScreenCastUtils.SCREEN_CAPTURE_REQUEST_CODE && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                screenCastController?.start(resultCode, data)
                isCasting = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isCasting) {
            screenCastController?.stop()
        }
    }
}
