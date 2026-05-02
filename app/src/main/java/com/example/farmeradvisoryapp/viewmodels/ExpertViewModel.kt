package com.example.farmeradvisoryapp.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeradvisoryapp.models.ChatMessage
import com.example.farmeradvisoryapp.models.ChatMessageEntity
import com.example.farmeradvisoryapp.models.Result
import com.example.farmeradvisoryapp.repositories.FarmingRepository
import com.example.farmeradvisoryapp.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class ExpertUiState {
    object Idle : ExpertUiState()
    object Loading : ExpertUiState()
    object Success : ExpertUiState()
    data class Error(val message: String) : ExpertUiState()
}

@HiltViewModel
class ExpertViewModel @Inject constructor(
    private val repository: FarmingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExpertUiState>(ExpertUiState.Idle)
    val uiState: StateFlow<ExpertUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _selectedImage = MutableStateFlow<Bitmap?>(null)
    val selectedImage: StateFlow<Bitmap?> = _selectedImage.asStateFlow()

    init {
        loadChatHistory()
    }

    private fun loadChatHistory() {
        viewModelScope.launch {
            try {
                repository.getChatHistory().collect { entities ->
                    _messages.value = entities.map { entity ->
                        ChatMessage(
                            text = entity.text,
                            isUser = entity.isUser,
                            timestamp = entity.timestamp
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading chat history")
            }
        }
    }

    fun sendMessage(text: String) {
        val validationError = ValidationUtils.getValidationError(text, 3, 500)
        if (validationError != null) {
            _uiState.value = ExpertUiState.Error(validationError)
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = ExpertUiState.Loading

                val userMessage = ChatMessage(text = text, isUser = true)
                _messages.value = _messages.value + userMessage

                repository.saveChatMessage(ChatMessageEntity(text = text, isUser = true))

                val result = repository.getFarmingAdvice(text, _selectedImage.value)

                when (result) {
                    is Result.Success -> {
                        val aiMessage = ChatMessage(text = result.data, isUser = false)
                        _messages.value = _messages.value + aiMessage

                        repository.saveChatMessage(ChatMessageEntity(text = result.data, isUser = false))

                        _uiState.value = ExpertUiState.Success
                        _selectedImage.value = null
                    }
                    is Result.Error -> {
                        _uiState.value = ExpertUiState.Error(result.message.ifEmpty { "Failed to get response" })
                    }
                    is Result.Loading -> {}
                }
            } catch (e: Exception) {
                _uiState.value = ExpertUiState.Error("Unexpected error: ${e.message}")
                Timber.e(e, "Unexpected error in sendMessage")
            }
        }
    }

    fun setSelectedImage(bitmap: Bitmap?) {
        _selectedImage.value = bitmap
    }

    fun clearMessages() {
        viewModelScope.launch {
            try {
                repository.deleteChatHistory()
                _messages.value = emptyList()
                _uiState.value = ExpertUiState.Idle
            } catch (e: Exception) {
                Timber.e(e, "Error clearing messages")
            }
        }
    }
}
