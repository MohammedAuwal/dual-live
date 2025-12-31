package com.duallive.app.data

import com.duallive.app.data.dao.LeagueDao
import com.duallive.app.data.entity.League
import com.duallive.app.data.entity.LeagueType
import kotlinx.coroutines.flow.Flow

class LeagueRepository(private val leagueDao: LeagueDao) {

    // Matches the ViewModel's call: repository.allLeagues
    val allLeagues: Flow<List<League>> = leagueDao.getAllLeagues()

    // Matches the ViewModel's call: repository.getLeaguesByType
    fun getLeaguesByType(type: LeagueType): Flow<List<League>> = 
        leagueDao.getLeaguesByType(type)

    // Matches the ViewModel's call: repository.insertLeague
    suspend fun insertLeague(league: League) {
        leagueDao.insertLeague(league)
    }

    // Matches the ViewModel's call: repository.deleteLeague
    suspend fun deleteLeague(league: League) {
        leagueDao.deleteLeague(league)
    }

    // Placeholder for future online search logic
    suspend fun findLeagueOnline(code: String): League? {
        return null
    }
}
