package com.example.farmeradvisoryapp.services

import android.content.Context
import android.graphics.Bitmap
import com.example.farmeradvisoryapp.BuildConfig
import com.example.farmeradvisoryapp.models.Result
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val apiKey = BuildConfig.GEMINI_API_KEY
    
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = apiKey
        )
    }

    suspend fun getFarmingAdvice(
        question: String,
        image: Bitmap? = null
    ): Result<String> = withContext(Dispatchers.Default) {
        return@withContext try {
            if (apiKey.isEmpty()) {
                return@withContext Result.Error(
                    exception = IllegalStateException("API key not configured"),
                    message = "Please configure GEMINI_API_KEY in local.properties"
                )
            }

            if (question.isBlank()) {
                return@withContext Result.Error(
                    exception = IllegalArgumentException("Question cannot be empty"),
                    message = "Please enter a valid question"
                )
            }

            val promptPrefix = """
                You are an expert farmer advisor for Indian agriculture.
                Provide practical, actionable advice based on Indian farming practices.
                Keep response concise (max 150 words).
            """.trimIndent()

            Timber.d("Sending request to Gemini API")

            val response = if (image != null) {
                val compressedImage = image.compressForAI()
                val inputContent = content {
                    image(compressedImage)
                    text("$promptPrefix\n\nAnalyze this image and answer: $question")
                }
                generativeModel.generateContent(inputContent)
            } else {
                generativeModel.generateContent("$promptPrefix\n\nQuestion: $question")
            }

            val responseText = response.text ?: "No response received from AI"
            
            Timber.d("Received response from Gemini")
            
            Result.Success(responseText)
        } catch (e: Exception) {
            Timber.e(e, "Error getting farming advice from Gemini")
            val errorMessage = when {
                e.message?.contains("API key", ignoreCase = true) == true -> "API key is invalid"
                e.message?.contains("timeout", ignoreCase = true) == true -> "Request timed out"
                e.message?.contains("network", ignoreCase = true) == true -> "Network error"
                else -> e.message ?: "Failed to get response"
            }
            Result.Error(exception = e, message = errorMessage)
        }
    }

    private fun Bitmap.compressForAI(): Bitmap {
        val maxDimension = 1024
        
        if (width <= maxDimension && height <= maxDimension) {
            return this
        }

        val scale = if (width > height) {
            maxDimension.toFloat() / width
        } else {
            maxDimension.toFloat() / height
        }

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return try {
            Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
        } catch (e: Exception) {
            Timber.e(e, "Error compressing bitmap")
            this
        }
    }
}
