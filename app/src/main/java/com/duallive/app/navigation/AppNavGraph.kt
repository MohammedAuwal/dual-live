package com.duallive.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.duallive.app.viewmodel.LeagueViewModel

@Composable
fun AppNavGraph(navController: NavHostController, leagueViewModel: LeagueViewModel) {
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
            composable("home") {
                HomeScreen(
                    onCreateLeague = { navController.navigate("create_league/CLASSIC") },
                    onJoinLeague = { /* To be implemented */ },
                    onViewLeagues = { navController.navigate("classic_list") }
                )
            }

            composable("classic_list") {
                val leagues by leagueViewModel.getLeaguesByType(LeagueType.CLASSIC).observeAsState(initial = emptyList())
                LeagueListScreen(
                    leagues = leagues,
                    type = LeagueType.CLASSIC,
                    onAddLeagueClick = { navController.navigate("create_league/CLASSIC") },
                    onLeagueClick = { league -> /* Navigate to Details */ },
                    onDeleteLeague = { league -> leagueViewModel.deleteLeague(league) }
                )
            }

            composable("ucl_list") {
                val leagues by leagueViewModel.getLeaguesByType(LeagueType.UCL).observeAsState(initial = emptyList())
                LeagueListScreen(
                    leagues = leagues,
                    type = LeagueType.UCL,
                    onAddLeagueClick = { navController.navigate("create_league/UCL") },
                    onLeagueClick = { league -> /* Navigate to Details */ },
                    onDeleteLeague = { league -> leagueViewModel.deleteLeague(league) }
                )
            }

            composable(
                route = "create_league/{leagueType}",
                arguments = listOf(navArgument("leagueType") { type = NavType.StringType })
            ) { backStackEntry ->
                val typeStr = backStackEntry.arguments?.getString("leagueType")
                val selectedType = if (typeStr == "UCL") LeagueType.UCL else LeagueType.CLASSIC
                
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
}
