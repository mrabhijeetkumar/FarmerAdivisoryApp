package com.example.farmeradvisoryapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeradvisoryapp.data.api.models.WeatherResponse
import com.example.farmeradvisoryapp.data.repositories.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val data: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _selectedCity = MutableStateFlow("Delhi")
    val selectedCity = _selectedCity.asStateFlow()

    fun fetchWeather(city: String) {
        val normalizedCity = city.trim().ifEmpty { "Delhi" }
        _selectedCity.value = normalizedCity

        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            repository.getWeather(normalizedCity)
                .onSuccess { _uiState.value = WeatherUiState.Success(it) }
                .onFailure { _uiState.value = WeatherUiState.Error(it.message ?: "Unknown error") }
        }
    }
}
