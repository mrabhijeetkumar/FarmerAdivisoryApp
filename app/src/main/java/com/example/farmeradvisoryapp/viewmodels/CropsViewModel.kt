package com.example.farmeradvisoryapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeradvisoryapp.models.CropData
import com.example.farmeradvisoryapp.repositories.FarmingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CropsViewModel @Inject constructor(
    private val repository: FarmingRepository
) : ViewModel() {

    private val _crops = MutableStateFlow<List<CropData>>(emptyList())
    val crops: StateFlow<List<CropData>> = _crops.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCrops()
    }

    private fun loadCrops() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _crops.value = listOf(
                    CropData("Wheat", "₹45k - ₹52k", "120 Days", "Rabi", 92),
                    CropData("Maize", "₹38k - ₹42k", "100 Days", "Kharif", 85),
                    CropData("Rice", "₹52k - ₹60k", "150 Days", "Kharif", 88),
                    CropData("Potato", "₹60k - ₹75k", "90 Days", "Rabi", 95),
                    CropData("Onion", "₹55k - ₹68k", "110 Days", "Rabi", 80),
                    CropData("Cotton", "₹42k - ₹48k", "180 Days", "Kharif", 78),
                    CropData("Sugarcane", "₹35k - ₹42k", "300 Days", "Kharif", 82)
                )
                _isLoading.value = false
            } catch (e: Exception) {
                Timber.e(e, "Error loading crops")
                _isLoading.value = false
            }
        }
    }
}
