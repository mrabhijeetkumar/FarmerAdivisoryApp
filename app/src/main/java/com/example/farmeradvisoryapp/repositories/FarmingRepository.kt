package com.example.farmeradvisoryapp.repositories

import com.example.farmeradvisoryapp.database.daos.ChatMessageDao
import com.example.farmeradvisoryapp.models.ChatMessageEntity
import com.example.farmeradvisoryapp.models.Result
import com.example.farmeradvisoryapp.services.GeminiService
import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FarmingRepository @Inject constructor(
    private val geminiService: GeminiService,
    private val chatMessageDao: ChatMessageDao
) {

    suspend fun saveChatMessage(message: ChatMessageEntity) {
        try {
            chatMessageDao.insertMessage(message)
            Timber.d("Chat message saved to database")
        } catch (e: Exception) {
            Timber.e(e, "Error saving chat message")
        }
    }

    fun getChatHistory(): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.getAllMessages()
    }

    suspend fun deleteChatHistory() {
        try {
            chatMessageDao.deleteAllMessages()
            Timber.d("Chat history cleared")
        } catch (e: Exception) {
            Timber.e(e, "Error deleting chat history")
        }
    }

    suspend fun getFarmingAdvice(question: String, image: Bitmap? = null): Result<String> {
        return geminiService.getFarmingAdvice(question, image)
    }
}
