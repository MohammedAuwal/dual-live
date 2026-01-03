package com.duallive.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel

import com.duallive.app.ui.home.HomeScreen
import com.duallive.app.ui.league.LeagueListScreen
import com.duallive.app.ui.league.CreateLeagueScreen
import com.duallive.app.data.entity.LeagueType
import com.duallive.app.viewmodel.LeagueViewModel

import com.duallive.app.ucl2026.ui.Ucl26RegistrationScreen
import com.duallive.app.ucl2026.ui.Ucl26LeagueScreen
import com.duallive.app.ucl2026.ui.Ucl26MatchScreen
import com.duallive.app.ucl2026.ui.Ucl26BracketScreen
import com.duallive.app.ucl2026.viewmodel.Ucl26ViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    leagueViewModel: LeagueViewModel,
    ucl26ViewModel: Ucl26ViewModel = viewModel()
) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                onNavigateToClassic = { navController.navigate("classic_list") },
                onNavigateToUCL = { navController.navigate("ucl_list") },
                onNavigateToNewUCL = { navController.navigate("swiss_list") },
                onJoinSubmit = { }
            )
        }

        composable("classic_list") {
            val leagues by leagueViewModel.getLeaguesByType(LeagueType.CLASSIC).observeAsState(initial = emptyList())
            LeagueListScreen(leagues = leagues, type = LeagueType.CLASSIC, onAddLeagueClick = { navController.navigate("create_league/CLASSIC") }, onLeagueClick = { navController.navigate("team_list/${it.id}") }, onDeleteLeague = { leagueViewModel.deleteLeague(it) })
        }

        composable("ucl_list") {
            val leagues by leagueViewModel.getLeaguesByType(LeagueType.UCL).observeAsState(initial = emptyList())
            LeagueListScreen(leagues = leagues, type = LeagueType.UCL, onAddLeagueClick = { navController.navigate("create_league/UCL") }, onLeagueClick = { navController.navigate("team_list/${it.id}") }, onDeleteLeague = { leagueViewModel.deleteLeague(it) })
        }

        composable("swiss_list") {
            val leagues by leagueViewModel.getLeaguesByType(LeagueType.SWISS).observeAsState(initial = emptyList())
            LeagueListScreen(leagues = leagues, type = LeagueType.SWISS, onAddLeagueClick = { navController.navigate("create_league/SWISS") }, onLeagueClick = { navController.navigate("new_ucl_league/${it.id}") }, onDeleteLeague = { leagueViewModel.deleteLeague(it) })
        }

        composable("create_league/{leagueType}") { backStackEntry ->
            val typeStr = backStackEntry.arguments?.getString("leagueType") ?: "CLASSIC"
            val selectedType = try { LeagueType.valueOf(typeStr) } catch(e: Exception) { LeagueType.CLASSIC }
            CreateLeagueScreen(
                preselectedType = selectedType,
                onSave = { name, desc, isHA, type ->
                    leagueViewModel.createLeague(name, desc, isHA, type)
                    if (type == LeagueType.SWISS) navController.navigate("new_ucl_team_registration/0")
                    else navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("new_ucl_team_registration/{leagueId}") {
            Ucl26RegistrationScreen(onTeamsConfirmed = { teamNames ->
                ucl26ViewModel.initializeTournament(teamNames)
                navController.navigate("new_ucl_matches")
            })
        }

        composable("new_ucl_matches") {
            Ucl26MatchScreen(viewModel = ucl26ViewModel, onBack = { navController.popBackStack() })
        }

        composable("new_ucl_league/{leagueId}") {
            val id = it.arguments?.getString("leagueId")?.toInt() ?: 0
            Ucl26LeagueScreen(
                leagueId = id, 
                viewModel = ucl26ViewModel, 
                onNavigateToMatches = { navController.navigate("new_ucl_matches") }, 
                onNavigateToBracket = { navController.navigate("new_ucl_bracket") },
                onBack = { navController.popBackStack() } // FIXED: Added missing onBack parameter
            )
        }

        composable("new_ucl_bracket") {
            Ucl26BracketScreen(viewModel = ucl26ViewModel, onBack = { navController.popBackStack() })
        }
    }
}
