package com.duallive.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel

// Base App Screens
import com.duallive.app.ui.home.HomeScreen
import com.duallive.app.ui.league.LeagueListScreen
import com.duallive.app.ui.league.CreateLeagueScreen
import com.duallive.app.data.entity.LeagueType
import com.duallive.app.viewmodel.LeagueViewModel

// === UCL 2026 FRESH SCREENS (CLEAN PACKAGE) ===
import com.duallive.app.ucl2026.ui.Ucl26RegistrationScreen
import com.duallive.app.ucl2026.ui.Ucl26LeagueScreen
import com.duallive.app.ucl2026.ui.Ucl26MatchScreen
import com.duallive.app.ucl2026.viewmodel.Ucl26ViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    leagueViewModel: LeagueViewModel
) {
    // Fresh ViewModel for the 36-team Swiss Model
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

        // --- CLASSIC LEAGUE (UNTOUCHED) ---
        composable("classic_list") {
            val leagues by leagueViewModel.getLeaguesByType(LeagueType.CLASSIC)
                .observeAsState(initial = emptyList())
            LeagueListScreen(
                leagues = leagues,
                type = LeagueType.CLASSIC,
                onAddLeagueClick = { navController.navigate("create_league/CLASSIC") },
                onLeagueClick = { /* Navigate to Details */ },
                onDeleteLeague = { leagueViewModel.deleteLeague(it) }
            )
        }

        // --- OLD UCL VERSION (KEEPING FOR COMPATIBILITY) ---
        composable("ucl_list") {
            val leagues by leagueViewModel.getLeaguesByType(LeagueType.UCL_OLD)
                .observeAsState(initial = emptyList())
            LeagueListScreen(
                leagues = leagues,
                type = LeagueType.UCL_OLD,
                onAddLeagueClick = { navController.navigate("create_league/UCL") },
                onLeagueClick = { /* Navigate to Details */ },
                onDeleteLeague = { leagueViewModel.deleteLeague(it) }
            )
        }

        composable(
            route = "create_league/{leagueType}",
            arguments = listOf(navArgument("leagueType") { type = NavType.StringType })
        ) { backStackEntry ->
            val typeStr = backStackEntry.arguments?.getString("leagueType") ?: "CLASSIC"
            val selectedType = LeagueType.valueOf(typeStr)

            CreateLeagueScreen(
                preselectedType = selectedType,
                onSave = { name, desc, isHA, type ->
                    leagueViewModel.createLeague(name, desc, isHA, type)
                    navController.popBackStack()
                }
            )
        }

        // --- NEW UCL 2026: BULK REGISTRATION ---
        composable("new_ucl_team_registration") {
            Ucl26RegistrationScreen(
                onTeamsConfirmed = { teamNames ->
                    ucl26ViewModel.initializeTournament(teamNames)
                    navController.navigate("new_ucl_league/1")
                }
            )
        }

        // --- NEW UCL 2026: LEAGUE PHASE TABLE ---
        composable(
            "new_ucl_league/{leagueId}",
            arguments = listOf(navArgument("leagueId") { type = NavType.IntType })
        ) { backStackEntry ->
            val leagueId = backStackEntry.arguments?.getInt("leagueId") ?: 0
            val teams by ucl26ViewModel.standings.collectAsState()

            Ucl26LeagueScreen(
                leagueId = leagueId,
                navController = navController,
                teams = teams
            )
        }

        // --- NEW UCL 2026: MATCH CENTER (SCORE ENTRY) ---
        composable("new_ucl_matches") {
            Ucl26MatchScreen(navController = navController, viewModel = ucl26ViewModel)
        }
    }
}
