package com.example.nofoodwaste.di

import com.example.nofoodwaste.ui.main.login.LoginPage
import com.example.nofoodwaste.ui.main.content.ProfilePage
import com.example.nofoodwaste.ui.main.login.RegisterPage
import com.example.nofoodwaste.di.modules.FirebaseModule
import com.example.nofoodwaste.di.modules.RepositoriesModule
import com.example.nofoodwaste.ui.main.content.MainFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FirebaseModule::class, RepositoriesModule::class])

interface NoFoodWasteComponent {

    fun inject( signUpFragment: LoginPage)

    fun inject( mainFragment: MainFragment)

    fun inject( registerFragment: RegisterPage)

    fun inject( profileFragment: ProfilePage)
}