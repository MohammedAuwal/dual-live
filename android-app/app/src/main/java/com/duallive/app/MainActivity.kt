package com.duallive.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.duallive.app.ui.league.CreateLeagueScreen
import com.duallive.app.data.AppDatabase
import com.duallive.app.data.entity.League
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val db = AppDatabase.getDatabase(this)

        setContent {
            MaterialTheme {
                Surface {
                    CreateLeagueScreen(onSave = { name, desc ->
                        // Simple offline save logic
                        MainScope().launch {
                            db.leagueDao().insertLeague(League(name = name, description = desc))
                            // For now, we just print to log; next we'll add navigation
                            println("League Saved: $name")
                        }
                    })
                }
            }
        }
    }
}
