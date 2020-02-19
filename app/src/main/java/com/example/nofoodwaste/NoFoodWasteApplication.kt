package com.example.nofoodwaste

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.example.nofoodwaste.di.ComponentInjector
import com.facebook.FacebookSdk
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig

class NoFoodWasteApplication: Application(), CameraXConfig.Provider {

    companion object{
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.fullyInitialize()
        context = applicationContext
        ComponentInjector.init()
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }

}