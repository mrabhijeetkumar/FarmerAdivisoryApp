package com.example.farmeradvisoryapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.farmeradvisoryapp.navigation.Screen
import com.example.farmeradvisoryapp.ui.components.BottomNavigationBar
import com.example.farmeradvisoryapp.ui.components.LoadingScreen
import com.example.farmeradvisoryapp.viewmodels.CropsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropsScreen(navController: NavController, viewModel: CropsViewModel = hiltViewModel()) {
    val crops by viewModel.crops.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recommended Crops", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = { BottomNavigationBar(navController, Screen.Crops.route) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (isLoading) {
            LoadingScreen("Finding best crops for you...")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column {
                        Text(
                            text = "Smart Recommendations",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Based on Delhi's current climate and soil",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                items(crops) { crop ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(crop.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    Text(crop.season, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                                
                                Surface(
                                    color = getScoreColor(crop.matchScore).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "${crop.matchScore}%", 
                                            color = getScoreColor(crop.matchScore), 
                                            style = MaterialTheme.typography.titleMedium, 
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text("Match", color = getScoreColor(crop.matchScore), style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                DetailItem("Duration", crop.duration)
                                DetailItem("Water", crop.waterNeeds)
                                DetailItem("Soil", crop.soilType)
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Est. Profit", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                        Text(crop.profit, color = Color(0xFF2E7D32), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = {},
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        Text("View Guide", style = MaterialTheme.typography.labelLarge)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

fun getScoreColor(score: Int): Color {
    return when {
        score >= 85 -> Color(0xFF2E7D32)
        score >= 70 -> Color(0xFFFFA000)
        else -> Color(0xFFD32F2F)
    }
}
