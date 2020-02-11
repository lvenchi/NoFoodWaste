package com.example.nofoodwaste

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.nofoodwaste.di.ComponentInjector
import com.example.nofoodwaste.ui.main.MainFragment
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val config: TwitterConfig = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig( TwitterAuthConfig(
                "rOF57fxvA2nehqmkdUuAXFcrw",
                "lqLuApYc3AoAMrNsuQodjUxnOQ3MDPmauMfWQACI6z632ksHnd")

            ).debug(true)
            .build()
        Twitter.initialize(config)
    }

}
