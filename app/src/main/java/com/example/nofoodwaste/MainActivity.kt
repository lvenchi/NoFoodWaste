package com.example.nofoodwaste

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import com.example.nofoodwaste.utils.Utils
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object LocaleHelper {

        fun updateLocale(context: Context?, locale: Locale) : Context{
            Locale.setDefault(locale)

            val config = context!!.resources.configuration
            config.setLocale(locale)
            Utils.currentLocale = locale
            context.resources.configuration.updateFrom(config)
            return context.createConfigurationContext(config)
        }

    }

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

    override fun attachBaseContext(base: Context?) {
        val locale = base?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)?.getString("locale", "en") ?: "en"
        base?.resources?.configuration?.setLocale(Locale.forLanguageTag(locale))
        super.attachBaseContext(updateLocale(base!!, Locale.forLanguageTag(locale)))
    }

    override fun onBackPressed() {
        if(findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.mainFragment){

        } else findNavController(R.id.nav_host_fragment).popBackStack()
    }

}
