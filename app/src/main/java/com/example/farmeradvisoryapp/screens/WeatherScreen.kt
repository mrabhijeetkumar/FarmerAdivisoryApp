package com.example.farmeradvisoryapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.farmeradvisoryapp.navigation.Screen
import com.example.farmeradvisoryapp.ui.components.BottomNavigationBar
import com.example.farmeradvisoryapp.ui.components.LoadingScreen
import com.example.farmeradvisoryapp.viewmodels.WeatherUiState
import com.example.farmeradvisoryapp.viewmodels.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(navController: NavController, viewModel: WeatherViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchWeather("Delhi")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Forecast", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = { BottomNavigationBar(navController, Screen.Weather.route) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (uiState) {
                is WeatherUiState.Loading -> LoadingScreen("Updating live forecast...")
                is WeatherUiState.Success -> {
                    val weather = (uiState as WeatherUiState.Success).data
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            WeatherMainCard(weather)
                        }

                        item {
                            Text("5-Day Forecast", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }

                        // Mock Forecast for UI completeness (Industry Level)
                        val mockForecast = listOf(
                            ForecastItem("Mon", "Cloudy", 28, 22),
                            ForecastItem("Tue", "Rainy", 25, 20),
                            ForecastItem("Wed", "Sunny", 32, 24),
                            ForecastItem("Thu", "Partly Cloudy", 30, 23),
                            ForecastItem("Fri", "Storm", 24, 19)
                        )

                        items(mockForecast) { day ->
                            ForecastRow(day)
                        }

                        item {
                            Spacer(Modifier.height(32.dp))
                        }
                    }
                }
                is WeatherUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Unable to fetch weather", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.fetchWeather("Delhi") }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherMainCard(weather: com.example.farmeradvisoryapp.data.api.models.WeatherResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("${weather.main.temp.toInt()}°C", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold)
                    Text(weather.weather.firstOrNull()?.description?.uppercase() ?: "CLEAR", style = MaterialTheme.typography.titleMedium)
                }
                Text("☀️", fontSize = 80.sp)
            }
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                WeatherMetric(Icons.Default.WaterDrop, "Humidity", "${weather.main.humidity}%")
                WeatherMetric(Icons.Default.Air, "Wind", "${weather.wind.speed} km/h")
                WeatherMetric(Icons.Default.Thermostat, "Feels", "${weather.main.temp.toInt()}°")
            }
        }
    }
}

@Composable
fun WeatherMetric(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(4.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ForecastRow(item: ForecastItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(item.day, fontWeight = FontWeight.Bold, modifier = Modifier.width(50.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (item.condition.contains("Rain")) "🌧️" else "🌤️", fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text(item.condition, style = MaterialTheme.typography.bodySmall)
            }
            Text("${item.max}° / ${item.min}°", fontWeight = FontWeight.SemiBold)
        }
    }
}

data class ForecastItem(val day: String, val condition: String, val max: Int, val min: Int)
