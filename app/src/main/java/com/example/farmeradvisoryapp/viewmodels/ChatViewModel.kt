package com.example.farmeradvisoryapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeradvisoryapp.BuildConfig
import com.example.farmeradvisoryapp.data.models.ChatMessage
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    fun sendMessage(text: String) {
        val userMessage = ChatMessage(text, true)
        _messages.value = _messages.value + userMessage

        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(text)
                val aiMessage = ChatMessage(response.text ?: "I'm sorry, I couldn't understand that.", false)
                _messages.value = _messages.value + aiMessage
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage("Error: ${e.message}", false)
            }
        }
    }
}
