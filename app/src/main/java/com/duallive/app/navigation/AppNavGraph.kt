package com.duallive.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.duallive.app.ui.home.HomeScreen
import com.duallive.app.ui.league.LeagueListScreen
import com.duallive.app.ui.league.CreateLeagueScreen
import com.duallive.app.data.entity.LeagueType
import com.duallive.app.viewmodel.LeagueViewModel

@Composable
fun AppNavGraph(navController: NavHostController, leagueViewModel: LeagueViewModel) {
    NavHost(navController = navController, startDestination = "home") {
        
        // 1. HomeScreen with correct parameter names
        composable("home") {
            HomeScreen(
                onNavigateToClassic = { navController.navigate("classic_list") },
                onNavigateToUCL = { navController.navigate("ucl_list") },
                onJoinSubmit = { code -> 
                    // Future logic for joining via code
                    println("Joining league with code: $code") 
                }
            )
        }

        // 2. Classic List
        composable("classic_list") {
            val leagues by leagueViewModel.getLeaguesByType(LeagueType.CLASSIC).observeAsState(initial = emptyList())
            LeagueListScreen(
                leagues = leagues,
                type = LeagueType.CLASSIC,
                onAddLeagueClick = { navController.navigate("create_league/CLASSIC") },
                onLeagueClick = { /* Navigate to Details */ },
                onDeleteLeague = { leagueViewModel.deleteLeague(it) }
            )
        }

        // 3. UCL List
        composable("ucl_list") {
            val leagues by leagueViewModel.getLeaguesByType(LeagueType.UCL).observeAsState(initial = emptyList())
            LeagueListScreen(
                leagues = leagues,
                type = LeagueType.UCL,
                onAddLeagueClick = { navController.navigate("create_league/UCL") },
                onLeagueClick = { /* Navigate to Details */ },
                onDeleteLeague = { leagueViewModel.deleteLeague(it) }
            )
        }

        // 4. Create Screen
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
    }
}
