package com.example.farmeradvisoryapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val farmLocation: String = "",
    val preferredLanguage: String = "en"
)

@Entity(tableName = "crops")
data class Crop(
    @PrimaryKey val id: String,
    val name: String,
    val season: String,
    val idealTemperature: Int,
    val waterNeeds: String,
    val soilType: String,
    val description: String,
    val imageUrl: String = ""
)

data class WeatherInfo(
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val city: String,
    val icon: String
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class MandiPrice(
    val commodity: String,
    val market: String,
    val minPrice: String,
    val maxPrice: String,
    val modalPrice: String,
    val date: String
)

data class DiseaseResult(
    val diseaseName: String,
    val confidence: Float,
    val treatment: String
)
