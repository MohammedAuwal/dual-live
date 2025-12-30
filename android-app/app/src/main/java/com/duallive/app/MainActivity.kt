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
        screenCastController = ScreenCastController(this)

        setContent {
            var currentScreen by rememberSaveable { mutableStateOf("league_list") }
            var selectedLeague by remember { mutableStateOf<League?>(null) }
            var winnerName by remember { mutableStateOf<String?>(null) }
            var liveCasting by rememberSaveable { mutableStateOf(false) }

            BackHandler(enabled = currentScreen != "league_list") {
                when (currentScreen) {
                    "team_list", "create_league" -> currentScreen = "league_list"
                    else -> currentScreen = "league_list"
                }
            }

            val leagues by db.leagueDao().getAllLeagues().collectAsState(initial = emptyList())

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        "league_list" -> LeagueListScreen(
                            leagues = leagues,
                            onLeagueClick = { league ->
                                selectedLeague = league
                                currentScreen = "team_list"
                            },
                            onDeleteLeague = { l -> scope.launch { db.leagueDao().deleteLeague(l) } },
                            onAddLeagueClick = { currentScreen = "create_league" }
                        )

                        // 🏆 THIS WAS MISSING - The Create League Screen
                        "create_league" -> CreateLeagueScreen(
                            onLeagueCreated = { name, desc, isHomeAway, type ->
                                scope.launch {
                                    val newLeague = League(
                                        name = name,
                                        description = desc,
                                        isHomeAndAway = isHomeAway,
                                        type = type
                                    )
                                    db.leagueDao().insertLeague(newLeague)
                                    currentScreen = "league_list"
                                }
                            },
                            onBack = { currentScreen = "league_list" }
                        )

                        "team_list" -> {
                            // Placeholder for your Team List UI
                            Text("Team List for ${selectedLeague?.name}")
                        }
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
        if (isCasting) screenCastController?.stop()
    }
}
