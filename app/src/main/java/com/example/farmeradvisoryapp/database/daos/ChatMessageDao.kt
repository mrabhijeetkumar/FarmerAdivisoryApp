package com.example.farmeradvisoryapp.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.farmeradvisoryapp.models.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Insert
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Delete
    suspend fun deleteMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE timestamp < :timestamp")
    suspend fun deleteOldMessages(timestamp: Long)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllMessages()

    @Query("SELECT COUNT(*) FROM chat_messages")
    suspend fun getMessageCount(): Int
}
