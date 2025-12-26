package com.duallive.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.duallive.app.data.dao.LeagueDao
import com.duallive.app.data.dao.TeamDao
import com.duallive.app.data.entity.League
import com.duallive.app.data.entity.Team
import com.duallive.app.data.entity.Standing

@Database(entities = [League::class, Team::class, Standing::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun leagueDao(): LeagueDao
    abstract fun teamDao(): TeamDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dual_live_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
