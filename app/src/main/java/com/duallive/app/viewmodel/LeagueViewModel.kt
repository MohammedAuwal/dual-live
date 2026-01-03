package com.duallive.app.viewmodel

import androidx.lifecycle.*
import com.duallive.app.data.LeagueRepository
import com.duallive.app.data.entity.League
import com.duallive.app.data.entity.LeagueType
import kotlinx.coroutines.launch

class LeagueViewModel(private val repository: LeagueRepository) : ViewModel() {

    val allLeagues: LiveData<List<League>> = repository.allLeagues.asLiveData()

    fun getLeaguesByType(type: LeagueType): LiveData<List<League>> {
        return repository.getLeaguesByType(type).asLiveData()
    }

    fun createLeague(name: String, description: String?, isHomeAndAway: Boolean, type: LeagueType) {
        viewModelScope.launch {
            // Generate permanent random code: e.g., DL-4921
            val randomDigits = (1000..9999).random()
            val newInviteCode = "DL-$randomDigits"
            
            val newLeague = League(
                name = name,
                description = description,
                isHomeAndAway = isHomeAndAway,
                type = type,
                inviteCode = newInviteCode
            )
            repository.insertLeague(newLeague)
        }
    }

    fun deleteLeague(league: League) {
        viewModelScope.launch {
            repository.deleteLeague(league)
        }
    }
}
