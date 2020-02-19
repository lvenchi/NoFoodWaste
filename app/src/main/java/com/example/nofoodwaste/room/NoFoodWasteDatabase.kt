package com.example.nofoodwaste.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nofoodwaste.models.Product
import com.example.nofoodwaste.models.User

@Database(entities = [Product::class, User::class], version = 1)
abstract class NoFoodWasteDatabase: RoomDatabase() {

    abstract fun getFoodDao() : NoFoodWasteDAO
}