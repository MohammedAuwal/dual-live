package com.duallive.app.data.dao

import androidx.room.*
import com.duallive.app.data.entity.League
import com.duallive.app.data.entity.LeagueType
import kotlinx.coroutines.flow.Flow

@Dao
interface LeagueDao {
    // This is the CRITICAL method for the Dual-Dashboard logic
    @Query("SELECT * FROM leagues WHERE type = :type ORDER BY id DESC")
    fun getLeaguesByType(type: LeagueType): Flow<List<League>>

    @Query("SELECT * FROM leagues ORDER BY id DESC")
    fun getAllLeagues(): Flow<List<League>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeague(league: League)

    @Delete
    suspend fun deleteLeague(league: League)
}
