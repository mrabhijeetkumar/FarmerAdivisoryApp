package com.example.farmeradvisoryapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.farmeradvisoryapp.screens.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Crops : Screen("crops")
    object Weather : Screen("weather")
    object Expert : Screen("expert")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Crops.route) { CropsScreen(navController) }
        composable(Screen.Weather.route) { WeatherScreen(navController) }
        composable(Screen.Expert.route) { ExpertScreen(navController) }
    }
}
