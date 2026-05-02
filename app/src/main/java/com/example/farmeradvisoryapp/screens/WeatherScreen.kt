package com.example.farmeradvisoryapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.farmeradvisoryapp.data.api.models.WeatherResponse
import com.example.farmeradvisoryapp.navigation.Screen
import com.example.farmeradvisoryapp.ui.components.BottomNavigationBar
import com.example.farmeradvisoryapp.ui.components.LoadingScreen
import com.example.farmeradvisoryapp.viewmodels.WeatherUiState
import com.example.farmeradvisoryapp.viewmodels.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(navController: NavController, viewModel: WeatherViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    var cityInput by rememberSaveable { mutableStateOf("Delhi") }

    LaunchedEffect(Unit) { viewModel.fetchWeather(selectedCity) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Weather & Farming Advisory", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = { BottomNavigationBar(navController, Screen.Weather.route) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = cityInput,
                onValueChange = { cityInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                label = { Text("Search city") },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { viewModel.fetchWeather(cityInput) }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )

            Spacer(Modifier.height(12.dp))

            when (uiState) {
                is WeatherUiState.Loading -> LoadingScreen("Updating live forecast...")
                is WeatherUiState.Success -> {
                    val weather = (uiState as WeatherUiState.Success).data
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item { WeatherMainCard(weather) }
                        item { FarmingAdvisoryCard(weather) }
                    }
                }
                is WeatherUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Unable to fetch weather", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.fetchWeather(selectedCity) }) { Text("Retry") }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherMainCard(weather: WeatherResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(24.dp)) {
            Text(weather.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("${weather.main.temp.toInt()}°C", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold)
                    Text(weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "Clear", style = MaterialTheme.typography.titleMedium)
                }
                Text("⛅", fontSize = 64.sp)
            }
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                WeatherMetric(Icons.Default.WaterDrop, "Humidity", "${weather.main.humidity}%")
                WeatherMetric(Icons.Default.Air, "Wind", "${weather.wind.speed} m/s")
                WeatherMetric(Icons.Default.Thermostat, "Feels", "${weather.main.feelsLike.toInt()}°")
            }
        }
    }
}

@Composable
private fun FarmingAdvisoryCard(weather: WeatherResponse) {
    val tips = remember(weather.main.temp, weather.main.humidity, weather.wind.speed) {
        buildList {
            if (weather.main.temp > 35) add("High heat: schedule irrigation early morning/evening.")
            if (weather.main.humidity > 80) add("High humidity: monitor fungal diseases and improve airflow.")
            if (weather.wind.speed > 8) add("Strong wind: avoid pesticide spray right now.")
            if (isEmpty()) add("Weather is stable: continue regular farm operations.")
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Actionable Farming Tips", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            tips.forEach { tip ->
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)) {
                    Text("• $tip", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun WeatherMetric(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(6.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}
