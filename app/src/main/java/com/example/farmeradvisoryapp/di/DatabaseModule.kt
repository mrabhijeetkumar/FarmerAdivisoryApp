package com.example.farmeradvisoryapp.di

import android.content.Context
import androidx.room.Room
import com.example.farmeradvisoryapp.database.FarmerAdvisoryDatabase
import com.example.farmeradvisoryapp.database.daos.ChatMessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): FarmerAdvisoryDatabase {
        return Room.databaseBuilder(
            context,
            FarmerAdvisoryDatabase::class.java,
            FarmerAdvisoryDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideChatMessageDao(
        database: FarmerAdvisoryDatabase
    ): ChatMessageDao {
        return database.chatMessageDao()
    }
}
