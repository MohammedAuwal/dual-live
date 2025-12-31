package com.duallive.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.duallive.app.ui.home.HomeScreen
import com.duallive.app.ui.league.LeagueListScreen
import com.duallive.app.ui.league.CreateLeagueScreen
import com.duallive.app.data.entity.LeagueType

@Composable
fun AppNavGraph(navController: NavHostController) {
    // Global Navy Background for all screens
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A192F), Color(0xFF040C1A))
                )
            )
    ) {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            // 1. Dashboard
            composable("home") {
                HomeScreen(
                    onNavigateToClassic = { navController.navigate("classic_list") },
                    onNavigateToUCL = { navController.navigate("ucl_list") }
                )
            }

            // 2. Classic League List
            composable("classic_list") {
                LeagueListScreen(
                    type = LeagueType.CLASSIC,
                    onAddLeagueClick = { navController.navigate("create_league/CLASSIC") },
                    onLeagueClick = { /* Navigate to league details */ },
                    onDeleteLeague = { /* Call ViewModel delete */ }
                )
            }

            // 3. UCL League List
            composable("ucl_list") {
                LeagueListScreen(
                    type = LeagueType.UCL,
                    onAddLeagueClick = { navController.navigate("create_league/UCL") },
                    onLeagueClick = { /* Navigate to league details */ },
                    onDeleteLeague = { /* Call ViewModel delete */ }
                )
            }

            // 4. Create League (Dynamic Type)
            composable(
                route = "create_league/{leagueType}",
                arguments = listOf(navArgument("leagueType") { type = NavType.StringType })
            ) { backStackEntry ->
                val typeStr = backStackEntry.arguments?.getString("leagueType")
                val selectedType = if (typeStr == "UCL") LeagueType.UCL else LeagueType.CLASSIC
                
                CreateLeagueScreen(
                    preselectedType = selectedType,
                    onSave = { name, desc, isHA, type ->
                        // Here you would call ViewModel.save()
                        navController.popBackStack() 
                    }
                )
            }
        }
    }
}
