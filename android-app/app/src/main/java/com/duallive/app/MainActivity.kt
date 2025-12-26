package com.duallive.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.duallive.app.ui.league.CreateLeagueScreen
import com.duallive.app.ui.league.LeagueListScreen
import com.duallive.app.data.AppDatabase
import com.duallive.app.data.entity.League
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)

        setContent {
            var currentScreen by remember { mutableStateOf("list") }
            val leagues by db.leagueDao().getAllLeagues().collectAsState(initial = emptyList())

            MaterialTheme {
                Surface {
                    when (currentScreen) {
                        "list" -> LeagueListScreen(
                            leagues = leagues,
                            onLeagueClick = { /* We will handle Team Management next */ },
                            onAddLeagueClick = { currentScreen = "create" }
                        )
                        "create" -> CreateLeagueScreen(onSave = { name, desc ->
                            MainScope().launch {
                                db.leagueDao().insertLeague(League(name = name, description = desc))
                                currentScreen = "list"
                            }
                        })
                    }
                }
            }
        }
    }
}
