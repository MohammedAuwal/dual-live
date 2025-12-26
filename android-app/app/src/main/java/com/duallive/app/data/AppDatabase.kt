package com.duallive.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.duallive.app.data.dao.*
import com.duallive.app.data.entity.*

@Database(
    entities = [League::class, Team::class, Match::class], 
    version = 2, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun leagueDao(): LeagueDao
    abstract fun teamDao(): TeamDao
    abstract fun matchDao(): MatchDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dual_live_db"
                )
                .fallbackToDestructiveMigration() // This handles the schema change by clearing old data
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
