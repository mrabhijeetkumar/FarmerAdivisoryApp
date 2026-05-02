package com.example.farmeradvisoryapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.farmeradvisoryapp.data.models.Crop
import com.example.farmeradvisoryapp.data.models.MandiPrice
import com.example.farmeradvisoryapp.data.models.WeatherInfo
import com.example.farmeradvisoryapp.navigation.Screen
import com.example.farmeradvisoryapp.ui.components.BottomNavigationBar
import com.example.farmeradvisoryapp.viewmodels.DashboardState
import com.example.farmeradvisoryapp.viewmodels.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: DashboardViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Smart Kisan", fontWeight = FontWeight.ExtraBold) },
                actions = {
                    IconButton(onClick = { /* Profile */ }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController, "home") }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (state) {
                is DashboardState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DashboardState.Success -> {
                    val data = (state as DashboardState.Success)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        item {
                            Text(
                                "Hello, Farmer! 🙏",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item {
                            WeatherSummaryCard(data.weather)
                        }

                        item {
                            AlertBanner()
                        }

                        item {
                            QuickActionsGrid(navController)
                        }

                        item {
                            SectionHeader("Current Mandi Prices", Icons.Default.TrendingUp)
                        }

                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(data.mandiPrices) { price ->
                                    MandiPriceCard(price)
                                }
                            }
                        }

                        item {
                            SectionHeader("Crop Suggestions", Icons.Default.Agriculture)
                        }

                        items(data.suggestions) { crop ->
                            CropDetailedCard(crop)
                        }
                    }
                }
                is DashboardState.Error -> {
                    Text("Error: ${(state as DashboardState.Error).message}")
                }
            }
        }
    }
}

@Composable
fun AlertBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Pest Alert!", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                Text("Locust activity reported in nearby districts.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun QuickActionsGrid(navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        QuickActionButton("Crop Dr.", Icons.Default.CameraAlt, Color(0xFFE91E63)) {
            navController.navigate(Screen.Expert.route)
        }
        QuickActionButton("Mandi", Icons.Default.Storefront, Color(0xFF4CAF50)) {}
        QuickActionButton("Soil", Icons.Default.Science, Color(0xFF2196F3)) {}
        QuickActionButton("Expert", Icons.Default.SupportAgent, Color(0xFFFF9800)) {
            navController.navigate(Screen.Expert.route)
        }
    }
}

@Composable
fun QuickActionButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(16.dp),
            color = color.copy(alpha = 0.1f),
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = color)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun MandiPriceCard(price: MandiPrice) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(price.commodity, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(price.market, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Text("₹${price.modalPrice}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            Text("/ Quintal", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun WeatherSummaryCard(weather: WeatherInfo) {
    val gradient = Brush.linearGradient(
        colors = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer)
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(modifier = Modifier.background(gradient).padding(24.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("${weather.temperature.toInt()}°C", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
                    Text(weather.condition.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("${weather.city} | Wind: ${weather.windSpeed} km/h", style = MaterialTheme.typography.bodySmall)
                }
                Text("🌤️", fontSize = 64.sp)
            }
        }
    }
}

@Composable
fun CropDetailedCard(crop: Crop) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🌾", fontSize = 32.sp)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(crop.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Ideal Season: ${crop.season}", style = MaterialTheme.typography.bodySmall)
                Text("Soil: ${crop.soilType}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
