package com.example.farmeradvisoryapp.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetectionState {
    object Idle : DetectionState()
    object Processing : DetectionState()
    data class Success(val labels: List<String>) : DetectionState()
    data class Error(val message: String) : DetectionState()
}

@HiltViewModel
class DiseaseDetectionViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<DetectionState>(DetectionState.Idle)
    val state = _state.asStateFlow()

    fun detectDisease(bitmap: Bitmap) {
        _state.value = DetectionState.Processing
        val image = InputImage.fromBitmap(bitmap, 0)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                val detected = labels.map { it.text }
                _state.value = DetectionState.Success(detected)
            }
            .addOnFailureListener {
                _state.value = DetectionState.Error(it.message ?: "Detection failed")
            }
    }
}
