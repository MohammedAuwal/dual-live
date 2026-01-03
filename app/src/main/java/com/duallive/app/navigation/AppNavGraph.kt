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
    leagueViewModel: LeagueViewModel
) {
    // ViewModel for the new 36-team Swiss system
    val ucl26ViewModel: Ucl26ViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {

        // --- HOME SCREEN ---
        composable("home") {
            HomeScreen(
                onNavigateToClassic = { navController.navigate("classic_list") },
                onNavigateToUCL = { navController.navigate("ucl_list") },
                onNavigateToNewUCL = { navController.navigate("new_ucl_team_registration") },
                onJoinSubmit = { code -> println("Joining league with code: $code") }
            )
        }

        // --- CLASSIC LEAGUE ---
        composable("classic_list") {
            val leagues by leagueViewModel.getLeaguesByType(LeagueType.CLASSIC).observeAsState(initial = emptyList())
            LeagueListScreen(
                leagues = leagues,
                type = LeagueType.CLASSIC,
                onAddLeagueClick = { navController.navigate("create_league/CLASSIC") },
                onLeagueClick = { },
                onDeleteLeague = { leagueViewModel.deleteLeague(it) }
            )
        }

        // --- OLD UCL VERSION ---
        composable("ucl_list") {
            val leagues by leagueViewModel.getLeaguesByType(LeagueType.UCL).observeAsState(initial = emptyList())
            LeagueListScreen(
                leagues = leagues,
                type = LeagueType.UCL,
                onAddLeagueClick = { navController.navigate("create_league/UCL") },
                onLeagueClick = { },
                onDeleteLeague = { leagueViewModel.deleteLeague(it) }
            )
        }

        // --- CREATE LEAGUE FLOW ---
        composable(
            route = "create_league/{leagueType}",
            arguments = listOf(navArgument("leagueType") { type = NavType.StringType })
        ) { backStackEntry ->
            val typeStr = backStackEntry.arguments?.getString("leagueType") ?: "CLASSIC"
            val selectedType = try { LeagueType.valueOf(typeStr) } catch(e: Exception) { LeagueType.CLASSIC }

            CreateLeagueScreen(
                preselectedType = selectedType,
                onSave = { name, desc, isHA, type ->
                    leagueViewModel.createLeague(name, desc, isHA, type)
                    navController.popBackStack()
                }
            )
        }

        // --- NEW UCL 2026: REGISTRATION ---
        composable("new_ucl_team_registration") {
            Ucl26RegistrationScreen(onTeamsConfirmed = { teamNames ->
                ucl26ViewModel.initializeTournament(teamNames)
                navController.navigate("new_ucl_league/1")
            })
        }

        // --- NEW UCL 2026: LEAGUE TABLE ---
        composable(
            "new_ucl_league/{leagueId}",
            arguments = listOf(navArgument("leagueId") { type = NavType.IntType })
        ) { backStackEntry ->
            val leagueId = backStackEntry.arguments?.getInt("leagueId") ?: 0
            Ucl26LeagueScreen(
                leagueId = leagueId,
                navController = navController,
                viewModel = ucl26ViewModel
            )
        }

        // --- NEW UCL 2026: MATCH CENTER ---
        composable("new_ucl_matches") {
            Ucl26MatchCenterScreen(navController = navController, viewModel = ucl26ViewModel)
        }

        // --- NEW UCL 2026: KNOCKOUT BRACKET ---
        composable("new_ucl_bracket") {
            Ucl26BracketScreen(viewModel = ucl26ViewModel)
        }
    }
}
