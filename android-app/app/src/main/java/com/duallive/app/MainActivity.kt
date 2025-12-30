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

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)
        val scope = MainScope()

        setContent {
            var currentScreen by rememberSaveable { mutableStateOf("league_list") }
            var selectedLeague by remember { mutableStateOf<League?>(null) }
            var showAddTeamDialog by remember { mutableStateOf(false) }
            var winnerName by remember { mutableStateOf<String?>(null) }

            // For match display
            var homeTeamId by remember { mutableStateOf<Int?>(null) }
            var awayTeamId by remember { mutableStateOf<Int?>(null) }
            var homeName by remember { mutableStateOf("") }
            var awayName by remember { mutableStateOf("") }
            var homeScore by remember { mutableStateOf(0) }
            var awayScore by remember { mutableStateOf(0) }
            var selectedFixture by remember { mutableStateOf<Fixture?>(null) }

            // For UCL
            var currentStageLabel by rememberSaveable { mutableStateOf("Group Stage") }
            var generatedFixtures by remember { mutableStateOf<List<Fixture>>(emptyList()) }
            var selectedGroup by rememberSaveable { mutableStateOf("A") }
            var uclStage by rememberSaveable { mutableStateOf("GROUP") }
            
            // For Classic League knockout
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
                    db.teamDao().getTeamsByLeague(it.id).collect { value = it }
                }
            }

            val matches by produceState<List<Match>>(initialValue = emptyList(), selectedLeague) {
                selectedLeague?.let {
                    db.matchDao().getMatchesByLeague(it.id).collect { value = it }
                }
            }

            val standings = remember(teams, matches) { TableCalculator.calculate(teams, matches) }

            // AUTOMATIC UCL PROGRESSION
            LaunchedEffect(matches, selectedLeague, uclStage, teams) {
                if (selectedLeague?.type == LeagueType.UCL && matches.isNotEmpty() && teams.isNotEmpty()) {
                    val completedMatches = matches.filter { it.homeScore >= 0 && it.awayScore >= 0 }
                    
                    when (uclStage) {
                        "GROUP" -> {
                            val groupMatches = completedMatches.filter { it.stage == "GROUP" }
                            val expectedGroupMatches = 8 * 6 * 2 // 8 groups × 6 matchdays × 2 matches
                            if (groupMatches.size >= expectedGroupMatches) {
                                scope.launch {
                                    val groups = teams.groupBy { it.groupName }
                                    val ro16Fixtures = FixtureGenerator.generateUCLRoundOf16Draw(groups)
                                    
                                    generatedFixtures = ro16Fixtures
                                    uclStage = "RO16"
                                    currentStageLabel = "Round of 16"
                                    currentScreen = "fixture_list"
                                }
                            }
                        }
                        "RO16" -> {
                            val ro16Matches = completedMatches.filter { it.stage == "RO16" }
                            if (ro16Matches.size >= 8 && ro16Matches.all { it.homeScore >= 0 && it.awayScore >= 0 }) {
                                val winners = FixtureGenerator.getWinnersFromKnockoutRound(ro16Matches, teams, "RO16")
                                if (winners.size == 8) {
                                    scope.launch {
                                        val qfFixtures = FixtureGenerator.generateNextKnockoutRound(winners, "QF")
                                        generatedFixtures = qfFixtures
                                        uclStage = "QF"
                                        currentStageLabel = "Quarter-Final"
                                        currentScreen = "fixture_list"
                                    }
                                }
                            }
                        }
                        "QF" -> {
                            val qfMatches = completedMatches.filter { it.stage == "QF" }
                            if (qfMatches.size >= 4 && qfMatches.all { it.homeScore >= 0 && it.awayScore >= 0 }) {
                                val winners = FixtureGenerator.getWinnersFromKnockoutRound(qfMatches, teams, "QF")
                                if (winners.size == 4) {
                                    scope.launch {
                                        val sfFixtures = FixtureGenerator.generateNextKnockoutRound(winners, "SF")
                                        generatedFixtures = sfFixtures
                                        uclStage = "SF"
                                        currentStageLabel = "Semi-Final"
                                        currentScreen = "fixture_list"
                                    }
                                }
                            }
                        }
                        "SF" -> {
                            val sfMatches = completedMatches.filter { it.stage == "SF" }
                            if (sfMatches.size >= 2 && sfMatches.all { it.homeScore >= 0 && it.awayScore >= 0 }) {
                                val winners = FixtureGenerator.getWinnersFromKnockoutRound(sfMatches, teams, "SF")
                                if (winners.size == 2) {
                                    scope.launch {
                                        val finalFixtures = FixtureGenerator.generateNextKnockoutRound(winners, "FINAL")
                                        generatedFixtures = finalFixtures
                                        uclStage = "FINAL"
                                        currentStageLabel = "Final"
                                        currentScreen = "fixture_list"
                                    }
                                }
                            }
                        }
                        "FINAL" -> {
                            val finalMatches = completedMatches.filter { it.stage == "FINAL" }
                            if (finalMatches.isNotEmpty() && finalMatches.all { it.homeScore >= 0 && it.awayScore >= 0 }) {
                                val finalMatch = finalMatches.first()
                                val homeTeam = teams.find { it.id == finalMatch.homeTeamId }
                                val awayTeam = teams.find { it.id == finalMatch.awayTeamId }
                                
                                val champion = if (finalMatch.homeScore > finalMatch.awayScore) {
                                    homeTeam
                                } else {
                                    awayTeam
                                }
                                champion?.let {
                                    winnerName = it.name
                                    uclStage = "COMPLETED"
                                }
                            }
                        }
                    }
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

                        "create_league" -> CreateLeagueScreen(
                            onSave = { name, desc, homeAway, leagueType ->
                                scope.launch {
                                    db.leagueDao().insertLeague(League(name = name, description = desc, isHomeAndAway = homeAway, type = leagueType))
                                    currentScreen = "league_list"
                                }
                            }
                        )

                        "team_list" -> {
                            Scaffold(
                                bottomBar = {
                                    Column {
                                        if (selectedLeague?.type == LeagueType.UCL) {
                                            var expanded by remember { mutableStateOf(false) }
                                            val groupOptions = listOf("A", "B", "C", "D", "E", "F", "G", "H")
                                            Box(modifier = Modifier.padding(8.dp)) {
                                                TextButton(onClick = { expanded = true }) {
                                                    Text("Group: $selectedGroup • Stage: $currentStageLabel")
                                                }
                                                DropdownMenu(
                                                    expanded = expanded,
                                                    onDismissRequest = { expanded = false }
                                                ) {
                                                    groupOptions.forEach { group ->
                                                        DropdownMenuItem(
                                                            text = { Text(group) },
                                                            onClick = {
                                                                selectedGroup = group
                                                                expanded = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        BottomAppBar {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                                // DRAW BUTTON
                                                TextButton(onClick = {
                                                    selectedLeague?.let { league ->
                                                        if (league.type == LeagueType.UCL) {
                                                            // UCL League
                                                            if (uclStage == "GROUP") {
                                                                val groups = teams.groupBy { it.groupName }
                                                                val groupTeams = groups[selectedGroup] ?: emptyList()
                                                                if (groupTeams.isNotEmpty()) {
                                                                    generatedFixtures = FixtureGenerator.generateUCLGroupFixtures(
                                                                        groupTeams, 
                                                                        league.isHomeAndAway
                                                                    )
                                                                }
                                                            } else {
                                                                // UCL Knockout - regenerate from existing matches
                                                                if (generatedFixtures.isEmpty()) {
                                                                    generatedFixtures = generateFixturesFromMatches(
                                                                        matches.filter { it.stage == uclStage }, 
                                                                        teams
                                                                    )
                                                                }
                                                            }
                                                        } else {
                                                            // Classic League
                                                            if (generatedFixtures.isEmpty()) {
                                                                generatedFixtures = FixtureGenerator.generateRoundRobin(
                                                                    teams, 
                                                                    league.isHomeAndAway
                                                                )
                                                            }
                                                        }
                                                        currentScreen = "fixture_list"
                                                    }
                                                }) { Text("DRAW") }

                                                TextButton(onClick = { currentScreen = "match_entry" }) { Text("MANUAL") }
                                                TextButton(onClick = { currentScreen = "match_history" }) { Text("RESULTS") }
                                                TextButton(onClick = { currentScreen = "standings" }) { Text("TABLE") }

                                                // NEXT/PROCEED BUTTON (For UCL only)
                                                if (selectedLeague?.type == LeagueType.UCL && uclStage != "COMPLETED") {
                                                    TextButton(
                                                        onClick = {
                                                            scope.launch {
                                                                when (uclStage) {
                                                                    "GROUP" -> {
                                                                        val groups = teams.groupBy { it.groupName }
                                                                        val ro16Fixtures = FixtureGenerator.generateUCLRoundOf16Draw(groups)
                                                                        generatedFixtures = ro16Fixtures
                                                                        uclStage = "RO16"
                                                                        currentStageLabel = "Round of 16"
                                                                        currentScreen = "fixture_list"
                                                                    }
                                                                    "RO16" -> {
                                                                        val ro16Matches = matches.filter { it.stage == "RO16" }
                                                                        val winners = FixtureGenerator.getWinnersFromKnockoutRound(ro16Matches, teams, "RO16")
                                                                        if (winners.isNotEmpty()) {
                                                                            val qfFixtures = FixtureGenerator.generateNextKnockoutRound(winners, "QF")
                                                                            generatedFixtures = qfFixtures
                                                                            uclStage = "QF"
                                                                            currentStageLabel = "Quarter-Final"
                                                                            currentScreen = "fixture_list"
                                                                        }
                                                                    }
                                                                    "QF" -> {
                                                                        val qfMatches = matches.filter { it.stage == "QF" }
                                                                        val winners = FixtureGenerator.getWinnersFromKnockoutRound(qfMatches, teams, "QF")
                                                                        if (winners.isNotEmpty()) {
                                                                            val sfFixtures = FixtureGenerator.generateNextKnockoutRound(winners, "SF")
                                                                            generatedFixtures = sfFixtures
                                                                            uclStage = "SF"
                                                                            currentStageLabel = "Semi-Final"
                                                                            currentScreen = "fixture_list"
                                                                        }
                                                                    }
                                                                    "SF" -> {
                                                                        val sfMatches = matches.filter { it.stage == "SF" }
                                                                        val winners = FixtureGenerator.getWinnersFromKnockoutRound(sfMatches, teams, "SF")
                                                                        if (winners.isNotEmpty()) {
                                                                            val finalFixtures = FixtureGenerator.generateNextKnockoutRound(winners, "FINAL")
                                                                            generatedFixtures = finalFixtures
                                                                            uclStage = "FINAL"
                                                                            currentStageLabel = "Final"
                                                                            currentScreen = "fixture_list"
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    ) { Text("NEXT") }
                                                }
                                                
                                                // Classic League knockout button
                                                if (selectedLeague?.type == LeagueType.CLASSIC && classicKnockoutStage.isEmpty()) {
                                                    TextButton(
                                                        onClick = {
                                                            classicKnockoutStage = "Knockout"
                                                            currentStageLabel = "Knockout Stage"
                                                            currentScreen = "knockout_select"
                                                        }
                                                    ) { Text("KNOCKOUT") }
                                                }
                                            }
                                        }
                                    }
                                }
                            ) { padding ->
                                Box(modifier = Modifier.padding(padding)) {
                                    TeamListScreen(
                                        leagueName = if (currentStageLabel.isEmpty()) selectedLeague?.name ?: "" 
                                                    else "${selectedLeague?.name?.substringBefore(" -")} - $currentStageLabel",
                                        teams = teams,
                                        isUcl = selectedLeague?.type == LeagueType.UCL,
                                        onBack = { currentScreen = "league_list" },
                                        onAddTeamClick = { showAddTeamDialog = true },
                                        onUpdateTeam = { updated -> scope.launch { db.teamDao().insertTeam(updated) } }
                                    )
                                }
                            }
                            if (showAddTeamDialog) {
                                AddTeamScreen(
                                    leagueType = selectedLeague?.type ?: LeagueType.CLASSIC,
                                    onSave = { names, group ->
                                        scope.launch {
                                            names.forEach { db.teamDao().insertTeam(Team(leagueId = selectedLeague!!.id, name = it, groupName = group)) }
                                            showAddTeamDialog = false
                                        }
                                    },
                                    onCancel = { showAddTeamDialog = false }
                                )
                            }
                        }

                        "fixture_list" -> {
                            val teamMap = teams.associateBy { it.id }
                            FixtureListScreen(
                                fixtures = generatedFixtures,
                                onBack = { currentScreen = "team_list" },
                                onFixtureClick = { fixture ->
                                    homeTeamId = fixture.homeTeam.id
                                    awayTeamId = fixture.awayTeam.id
                                    homeName = fixture.homeTeam.name
                                    awayName = fixture.awayTeam.name
                                    homeScore = 0
                                    awayScore = 0
                                    selectedFixture = fixture
                                    currentScreen = "live_display"
                                }
                            )
                        }

                        "live_display" -> {
                            val teamMap = teams.associateBy { it.id }
                            MatchDisplayScreen(
                                homeName = homeName,
                                awayName = awayName,
                                homeScore = homeScore,
                                awayScore = awayScore,
                                onUpdateHome = { hScore -> homeScore = hScore },
                                onUpdateAway = { aScore -> awayScore = aScore },
                                onSaveAndClose = {
                                    scope.launch {
                                        selectedLeague?.let { league ->
                                            selectedFixture?.let { fixture ->
                                                val stage = if (league.type == LeagueType.UCL) {
                                                    when (uclStage) {
                                                        "RO16" -> "RO16"
                                                        "QF" -> "QF"
                                                        "SF" -> "SF"
                                                        "FINAL" -> "FINAL"
                                                        else -> "GROUP"
                                                    }
                                                } else {
                                                    if (fixture.isKnockout) "KNOCKOUT" else ""
                                                }
                                                
                                                db.matchDao().insertMatch(
                                                    Match(
                                                        leagueId = league.id,
                                                        homeTeamId = fixture.homeTeam.id,
                                                        awayTeamId = fixture.awayTeam.id,
                                                        homeScore = homeScore,
                                                        awayScore = awayScore,
                                                        stage = stage,
                                                        isKnockout = fixture.isKnockout
                                                    )
                                                )
                                            }
                                        }
                                        currentScreen = "fixture_list"
                                    }
                                },
                                onCancel = { currentScreen = "fixture_list" }
                            )
                        }

                        "match_history" -> {
                            val teamMap = teams.associateBy { it.id }
                            MatchHistoryScreen(
                                matches = matches,
                                teams = teams,
                                onBack = { currentScreen = "team_list" },
                                onDeleteMatch = { match ->
                                    scope.launch {
                                        db.matchDao().deleteMatch(match)
                                    }
                                },
                                onUpdateMatch = { match, newHomeScore, newAwayScore ->
                                    scope.launch {
                                        val updatedMatch = match.copy(
                                            homeScore = newHomeScore,
                                            awayScore = newAwayScore
                                        )
                                        db.matchDao().insertMatch(updatedMatch)
                                    }
                                }
                            )
                        }

                        "match_entry" -> {
                            val teamMap = teams.associateBy { it.id }
                            MatchEntryScreen(
                                teams = teams,
                                onSave = { homeTeam: Team, awayTeam: Team, homeScore: Int, awayScore: Int ->
                                    scope.launch {
                                        selectedLeague?.let { league ->
                                            val stage = if (league.type == LeagueType.UCL) uclStage else ""
                                            db.matchDao().insertMatch(
                                                Match(
                                                    leagueId = league.id,
                                                    homeTeamId = homeTeam.id,
                                                    awayTeamId = awayTeam.id,
                                                    homeScore = homeScore,
                                                    awayScore = awayScore,
                                                    stage = stage,
                                                    isKnockout = (league.type == LeagueType.CLASSIC && classicKnockoutStage.isNotEmpty())
                                                )
                                            )
                                        }
                                    }
                                },
                                onBack = { currentScreen = "team_list" }
                            )
                        }

                        "standings" -> StandingsScreen(
                            standings = standings,
                            onBack = { currentScreen = "team_list" }
                        )

                        "knockout_select" -> KnockoutSelectionScreen(
                            teams = teams,
                            onProceed = { selectedTeams: List<Team> ->
                                scope.launch {
                                    generatedFixtures = FixtureGenerator.generateKnockoutDraw(selectedTeams, "Knockout")
                                    classicKnockoutStage = "Knockout"
                                    currentStageLabel = "Knockout Stage"
                                    currentScreen = "fixture_list"
                                }
                            },
                            onBack = { 
                                classicKnockoutStage = ""
                                currentStageLabel = ""
                                currentScreen = "team_list" 
                            }
                        )
                    }
                }
            }
        }
    }
    
    private fun generateFixturesFromMatches(matches: List<Match>, teams: List<Team>): List<Fixture> {
        val teamMap = teams.associateBy { it.id }
        val fixtures = mutableListOf<Fixture>()
        
        for (match in matches) {
            val homeTeam = teamMap[match.homeTeamId]
            val awayTeam = teamMap[match.awayTeamId]
            
            if (homeTeam != null && awayTeam != null) {
                fixtures.add(Fixture(
                    round = 1,
                    homeTeam = homeTeam,
                    awayTeam = awayTeam,
                    label = match.stage,
                    stage = match.stage,
                    isKnockout = match.isKnockout
                ))
            }
        }
        
        return fixtures
    }
}
