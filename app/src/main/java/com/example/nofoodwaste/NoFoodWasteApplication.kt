package com.example.nofoodwaste

import android.app.Application
import android.content.Context
import com.example.nofoodwaste.di.ComponentInjector
import com.facebook.FacebookSdk

class NoFoodWasteApplication: Application() {

    companion object{
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.fullyInitialize()
        context = applicationContext
        ComponentInjector.init()
    }

}