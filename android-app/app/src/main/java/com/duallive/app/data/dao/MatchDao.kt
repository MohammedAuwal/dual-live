package com.duallive.app.data.dao

import androidx.room.*
import com.duallive.app.data.entity.Match
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches WHERE leagueId = :leagueId ORDER BY id DESC")
    fun getMatchesByLeague(leagueId: Int): Flow<List<Match>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: Match)
}
