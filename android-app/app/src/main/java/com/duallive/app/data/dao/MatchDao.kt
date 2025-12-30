package com.duallive.app.data.dao

import androidx.room.*
import com.duallive.app.data.entity.Match
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: Match)

    @Query("SELECT * FROM matches WHERE leagueId = :leagueId")
    fun getMatchesByLeague(leagueId: Long): Flow<List<Match>>

    @Delete
    suspend fun deleteMatch(match: Match)

    @Query("DELETE FROM matches WHERE leagueId = :leagueId")
    suspend fun deleteMatchesByLeague(leagueId: Long)
}
