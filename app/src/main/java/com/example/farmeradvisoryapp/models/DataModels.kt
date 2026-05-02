package com.example.farmeradvisoryapp.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val image: Bitmap? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String? = null
)

data class FarmingTip(
    val icon: String,
    val title: String,
    val description: String
)

data class WeatherData(
    val temperature: Int,
    val condition: String,
    val humidity: Int,
    val windSpeed: Int = 0,
    val feelsLike: Int = 0,
    val location: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class ForecastData(
    val day: String,
    val max: Int,
    val min: Int,
    val condition: String,
    val humidity: Int = 0,
    val rainChance: Int = 0
)

data class Alert(
    val id: Int,
    val type: String,
    val severity: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val affectsCrops: List<String> = emptyList()
)

data class CropData(
    val name: String,
    val profit: String,
    val duration: String,
    val season: String,
    val matchScore: Int,
    val waterNeeds: String = "Medium",
    val soilType: String = "Loamy",
    val bestMonth: String = ""
)

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val message: String = "") : Result<Nothing>()
    object Loading : Result<Nothing>()
}

@Entity(tableName = "fields")
data class Field(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val area: Double,
    val soilType: String,
    val location: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val currentCrop: String? = null,
    val createdDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val name: String = "",
    val farmLocation: String = "",
    val farmArea: Double = 0.0
)
