package com.example.farmeradvisoryapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeradvisoryapp.data.models.Crop
import com.example.farmeradvisoryapp.data.models.MandiPrice
import com.example.farmeradvisoryapp.data.models.WeatherInfo
import com.example.farmeradvisoryapp.data.repositories.MandiRepository
import com.example.farmeradvisoryapp.data.repositories.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(
        val weather: WeatherInfo,
        val suggestions: List<Crop>,
        val mandiPrices: List<MandiPrice>
    ) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val mandiRepository: MandiRepository
) : ViewModel() {

    private val _state = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val state = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _state.value = DashboardState.Loading
            // In a real app, we'd get the user's location
            weatherRepository.getWeather("Delhi").onSuccess { weather ->
                val suggestions = getDummySuggestions() // Replace with ML logic
                _state.value = DashboardState.Success(
                    weather = com.example.farmeradvisoryapp.data.models.WeatherInfo(
                        temperature = weather.main.temp,
                        condition = weather.weather.firstOrNull()?.description ?: "Unknown",
                        humidity = weather.main.humidity,
                        windSpeed = weather.wind.speed,
                        city = weather.name,
                        icon = weather.weather.firstOrNull()?.icon ?: ""
                    ),
                    suggestions = suggestions,
                    mandiPrices = mandiRepository.getLatestPrices()
                )
            }.onFailure {
                _state.value = DashboardState.Error(it.message ?: "Failed to load data")
            }
        }
    }

    private fun getDummySuggestions(): List<Crop> {
        return listOf(
            Crop("1", "Wheat", "Winter", 20, "Moderate", "Alluvial", "Best for northern India"),
            Crop("2", "Rice", "Monsoon", 30, "High", "Clayey", "Requires lots of water")
        )
    }
}
