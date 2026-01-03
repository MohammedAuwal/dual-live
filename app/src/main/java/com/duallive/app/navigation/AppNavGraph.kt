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

        // --- HOME SCREEN ---
        composable("home") {
            HomeScreen(
                onNavigateToClassic = { navController.navigate("classic_list") },
                onNavigateToUCL = { navController.navigate("ucl_list") },
                onNavigateToNewUCL = { navController.navigate("swiss_list") },
                onJoinSubmit = { code -> println("Joining league with code: $code") }
            )
        }

        // --- CLASSIC LEAGUE LIST ---
        composable("classic_list") {
            val leagues by leagueViewModel.getLeaguesByType(LeagueType.CLASSIC).observeAsState(initial = emptyList())
            LeagueListScreen(
                leagues = leagues,
                type = LeagueType.CLASSIC,
                onAddLeagueClick = { navController.navigate("create_league/CLASSIC") },
                onLeagueClick = { league -> 
                    // Make sure "team_list" exists or change to match screen
                    navController.navigate("team_list/${league.id}") 
                },
                onDeleteLeague = { leagueViewModel.deleteLeague(it) }
            )
        }

        // --- OLD UCL VERSION LIST ---
        composable("ucl_list") {
            val leagues by leagueViewModel.getLeaguesByType(LeagueType.UCL).observeAsState(initial = emptyList())
            LeagueListScreen(
                leagues = leagues,
                type = LeagueType.UCL,
                onAddLeagueClick = { navController.navigate("create_league/UCL") },
                onLeagueClick = { league -> 
                    navController.navigate("team_list/${league.id}") 
                },
                onDeleteLeague = { leagueViewModel.deleteLeague(it) }
            )
        }

        // --- NEW SWISS UCL LIST ---
        composable("swiss_list") {
            val leagues by leagueViewModel.getLeaguesByType(LeagueType.SWISS).observeAsState(initial = emptyList())
            LeagueListScreen(
                leagues = leagues,
                type = LeagueType.SWISS,
                onAddLeagueClick = { navController.navigate("create_league/SWISS") },
                onLeagueClick = { league -> 
                    // ACTION: Go straight to the Match Center where scores are!
                    navController.navigate("new_ucl_matches") 
                },
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
                    // After creating a Swiss league, go to registration
                    if (type == LeagueType.SWISS) {
                        navController.navigate("new_ucl_team_registration/0")
                    } else {
                        navController.popBackStack()
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- NEW UCL 2026: REGISTRATION ---
        composable(
            "new_ucl_team_registration/{leagueId}",
            arguments = listOf(navArgument("leagueId") { type = NavType.IntType })
        ) {
            Ucl26RegistrationScreen(onTeamsConfirmed = { teamNames ->
                ucl26ViewModel.initializeTournament(teamNames)
                // DIRECT REDIRECT: Registration -> Scores (Not boring table)
                navController.navigate("new_ucl_matches") {
                    popUpTo("swiss_list") { inclusive = false }
                }
            })
        }

        // --- NEW UCL 2026: MATCH CENTER (THE ACTION) ---
        composable("new_ucl_matches") {
            Ucl26MatchScreen(
                viewModel = ucl26ViewModel, 
                onBack = { navController.navigate("swiss_list") }
            )
        }

        // --- NEW UCL 2026: LEAGUE TABLE ---
        composable(
            "new_ucl_league/{leagueId}",
            arguments = listOf(navArgument("leagueId") { type = NavType.IntType })
        ) { backStackEntry ->
            val leagueId = backStackEntry.arguments?.getInt("leagueId") ?: 0
            Ucl26LeagueScreen(
                leagueId = leagueId,
                viewModel = ucl26ViewModel,
                onNavigateToMatches = { navController.navigate("new_ucl_matches") },
                onNavigateToBracket = { navController.navigate("new_ucl_bracket") }
            )
        }

        // --- NEW UCL 2026: KNOCKOUT BRACKET ---
        composable("new_ucl_bracket") {
            Ucl26BracketScreen(
                viewModel = ucl26ViewModel, 
                onBack = { navController.popBackStack() }
            )
        }
    }
}
