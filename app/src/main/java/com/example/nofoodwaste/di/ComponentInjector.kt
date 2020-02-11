package com.example.nofoodwaste.di


abstract class ComponentInjector {

    companion object {
        lateinit var component: NoFoodWasteComponent

        fun init(){
            component = DaggerNoFoodWasteComponent.builder().build()
        }
    }


}