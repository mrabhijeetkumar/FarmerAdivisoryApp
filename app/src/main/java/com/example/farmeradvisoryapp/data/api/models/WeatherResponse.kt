package com.example.farmeradvisoryapp.data.api.models

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("main") val main: Main,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("name") val name: String
)

data class Main(
    @SerializedName("temp") val temp: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("pressure") val pressure: Int
)

data class Weather(
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

data class Wind(
    @SerializedName("speed") val speed: Double
)
