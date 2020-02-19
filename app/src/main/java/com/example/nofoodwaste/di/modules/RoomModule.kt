package com.example.nofoodwaste.di.modules

import androidx.room.Room
import com.example.nofoodwaste.NoFoodWasteApplication
import com.example.nofoodwaste.repositories.RoomRepositoryImpl
import com.example.nofoodwaste.room.NoFoodWasteDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {

    @Singleton
    private val noFoodWasteDatabase by lazy {
        Room.databaseBuilder(NoFoodWasteApplication.context, NoFoodWasteDatabase::class.java, "UserFoodDatabase")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRoomRepository() : RoomRepositoryImpl = RoomRepositoryImpl(noFoodWasteDatabase.getFoodDao())

}