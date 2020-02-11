package com.example.nofoodwaste.di

import com.example.nofoodwaste.LoginPage
import com.example.nofoodwaste.RegisterPage
import com.example.nofoodwaste.di.modules.FirebaseModule
import com.example.nofoodwaste.di.modules.RepositoriesModule
import com.example.nofoodwaste.ui.main.MainFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FirebaseModule::class, RepositoriesModule::class])

interface NoFoodWasteComponent {

    fun inject( signUpFragment: LoginPage )

    fun inject( mainFragment: MainFragment )

    fun inject( registerFragment: RegisterPage )
}