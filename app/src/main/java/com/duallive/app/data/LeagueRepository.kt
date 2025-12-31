package com.duallive.app.data

import com.duallive.app.data.entity.League
import com.duallive.app.data.dao.LeagueDao

class LeagueRepository(private val leagueDao: LeagueDao) {

    // Saves locally to Room (Works 100% Offline)
    suspend fun createLeague(league: League) {
        leagueDao.insertLeague(league)
    }

    // Since Firebase is disabled, we return null for online searches for now
    suspend fun findLeagueOnline(code: String): League? {
        return null
    }
}
