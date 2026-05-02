package com.example.farmeradvisoryapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.TypeConverter
import com.example.farmeradvisoryapp.database.daos.ChatMessageDao
import com.example.farmeradvisoryapp.models.ChatMessageEntity
import com.example.farmeradvisoryapp.models.Field
import com.example.farmeradvisoryapp.models.Notification
import com.example.farmeradvisoryapp.models.UserProfile
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Database(
    entities = [
        ChatMessageEntity::class,
        Field::class,
        Notification::class,
        UserProfile::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FarmerAdvisoryDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        const val DATABASE_NAME = "farmer_advisory_db"
    }
}

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { 
            try {
                Json.decodeFromString(it)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
