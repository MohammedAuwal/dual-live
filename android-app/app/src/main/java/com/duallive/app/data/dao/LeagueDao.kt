package com.duallive.app.data.dao

import androidx.room.*
import com.duallive.app.data.entity.League
import kotlinx.coroutines.flow.Flow

@Dao
interface LeagueDao {
    @Query("SELECT * FROM leagues ORDER BY createdDate DESC")
    fun getAllLeagues(): Flow<List<League>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeague(league: League): Long

    @Delete
    suspend fun deleteLeague(league: League)
}
