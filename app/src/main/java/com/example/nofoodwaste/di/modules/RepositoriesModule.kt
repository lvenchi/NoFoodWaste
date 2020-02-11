package com.example.nofoodwaste.di.modules

import com.example.nofoodwaste.repositories.FirebaseRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoriesModule {

    @Singleton
    @Provides
    fun provideFirebaseRepository(
        firebaseAuth: FirebaseAuth,
        firebaseDatabase: DatabaseReference
    ): FirebaseRepositoryImpl = FirebaseRepositoryImpl(firebaseAuth, firebaseDatabase)
}