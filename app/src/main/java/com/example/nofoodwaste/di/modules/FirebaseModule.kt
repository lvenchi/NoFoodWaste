package com.example.nofoodwaste.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides


@Module
class FirebaseModule {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val firebaseDatabase = FirebaseDatabase.getInstance().reference

    @Provides
    fun provideFirebaseAuth() : FirebaseAuth {
        return firebaseAuth
    }

    @Provides
    fun provideFirebaseDatabase() : DatabaseReference {
        return firebaseDatabase
    }

}