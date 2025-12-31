package com.duallive.app.data.dao

import androidx.room.*
import com.duallive.app.data.entity.League
import kotlinx.coroutines.flow.Flow

@Dao
interface LeagueDao {
    @Query("SELECT * FROM leagues WHERE type = :type")
    fun getLeaguesByType(type: LeagueType): Flow<List<League>>
    @Query("SELECT * FROM leagues ORDER BY id DESC")
    fun getAllLeagues(): Flow<List<League>>

    @Insert
    suspend fun insertLeague(league: League)

    @Delete
    suspend fun deleteLeague(league: League)
}
