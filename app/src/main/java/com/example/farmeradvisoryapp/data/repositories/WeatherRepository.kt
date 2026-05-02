package com.example.farmeradvisoryapp.data.repositories

import com.example.farmeradvisoryapp.BuildConfig
import com.example.farmeradvisoryapp.data.api.WeatherApiService
import com.example.farmeradvisoryapp.data.api.models.WeatherResponse
import javax.inject.Inject
import javax.inject.Singleton

interface WeatherRepository {
    suspend fun getWeather(city: String): Result<WeatherResponse>
}

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService
) : WeatherRepository {
    
    override suspend fun getWeather(city: String): Result<WeatherResponse> {
        if (BuildConfig.WEATHER_API_KEY.isEmpty() || BuildConfig.WEATHER_API_KEY == "paste_your_api_key_here") {
            return Result.failure(Exception("Please add WEATHER_API_KEY to local.properties"))
        }
        return try {
            val response = apiService.getWeather(city, BuildConfig.WEATHER_API_KEY)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
