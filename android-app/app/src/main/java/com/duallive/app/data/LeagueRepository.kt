package com.duallive.app.data

import com.duallive.app.data.entity.League
import com.duallive.app.data.dao.LeagueDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LeagueRepository(private val leagueDao: LeagueDao) {
    private val firestore = FirebaseFirestore.getInstance()

    // 1. Save locally (Works instantly offline)
    // 2. Sync to Firebase (Automatically uploads when back online)
    suspend fun createLeague(league: League) {
        // Save to Room
        leagueDao.insertLeague(league)
        
        // Push to Firebase
        val leagueMap = hashMapOf(
            "id" to league.id,
            "name" to league.name,
            "inviteCode" to league.inviteCode,
            "type" to league.type.name,
            "description" to league.description
        )
        
        firestore.collection("leagues")
            .document(league.inviteCode)
            .set(leagueMap)
    }

    // Find a league online to join
    suspend fun findLeagueOnline(code: String): League? {
        return try {
            val snapshot = firestore.collection("leagues")
                .document(code)
                .get()
                .await()
            
            if (snapshot.exists()) {
                // Convert Firestore data back to our League object
                // Logic to return League goes here
                null 
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
