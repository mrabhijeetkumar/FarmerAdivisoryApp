package com.example.farmeradvisoryapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeradvisoryapp.models.FarmingTip
import com.example.farmeradvisoryapp.models.WeatherData
import com.example.farmeradvisoryapp.repositories.FarmingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FarmingRepository
) : ViewModel() {

    private val _farmingTips = MutableStateFlow<List<FarmingTip>>(emptyList())
    val farmingTips: StateFlow<List<FarmingTip>> = _farmingTips.asStateFlow()

    private val _weatherData = MutableStateFlow<WeatherData?>(null)
    val weatherData: StateFlow<WeatherData?> = _weatherData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                loadTips()
                loadWeatherData()
                _isLoading.value = false
            } catch (e: Exception) {
                Timber.e(e, "Error loading home data")
                _isLoading.value = false
            }
        }
    }

    private fun loadTips() {
        _farmingTips.value = listOf(
            FarmingTip("🌾", "Soil Testing", "Test your soil NPK levels every season"),
            FarmingTip("💧", "Irrigation", "Water early morning for maximum absorption"),
            FarmingTip("🐛", "Pest Management", "Use integrated pest management techniques"),
            FarmingTip("🌱", "Crop Rotation", "Rotate crops to maintain soil health"),
            FarmingTip("☀️", "Weather Monitoring", "Check weather before sowing and harvesting")
        )
    }

    private fun loadWeatherData() {
        _weatherData.value = WeatherData(
            temperature = 28,
            condition = "Partly Cloudy",
            humidity = 65,
            windSpeed = 12,
            feelsLike = 30,
            location = "Your Farm"
        )
    }
}
