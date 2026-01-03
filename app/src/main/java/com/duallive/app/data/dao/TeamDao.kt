package com.duallive.app.data.dao

import androidx.room.*
import com.duallive.app.data.entity.Team
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams WHERE leagueId = :leagueId")
    fun getTeamsByLeague(leagueId: Int): Flow<List<Team>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: Team)

    @Delete
    suspend fun deleteTeam(team: Team)

    @Query("SELECT COUNT(*) FROM teams WHERE leagueId = :leagueId")
    suspend fun getTeamCountForLeague(leagueId: Int): Int
}
