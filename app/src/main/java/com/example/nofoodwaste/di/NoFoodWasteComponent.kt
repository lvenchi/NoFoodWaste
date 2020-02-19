package com.example.nofoodwaste.di

import com.example.nofoodwaste.ui.main.login.LoginPage
import com.example.nofoodwaste.ui.main.login.RegisterPage
import com.example.nofoodwaste.di.modules.FirebaseModule
import com.example.nofoodwaste.di.modules.RepositoriesModule
import com.example.nofoodwaste.ui.main.content.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FirebaseModule::class, RepositoriesModule::class])

interface NoFoodWasteComponent {

    fun inject( signUpFragment: LoginPage)

    fun inject( mainFragment: MainFragment)

    fun inject( registerFragment: RegisterPage)

    fun inject( profileFragment: ProfilePage)

    fun inject( datepickerFragment: DatepickerFragment)

    fun inject( productsPage: ProductsPage)

    fun inject( scanPage: ScanPage)
}