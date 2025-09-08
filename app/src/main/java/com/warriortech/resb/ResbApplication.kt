package com.warriortech.resb

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.warriortech.resb.util.LocaleHelper
import com.warriortech.resb.util.SubscriptionScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.BuildConfig
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ResbApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var subscriptionScheduler: SubscriptionScheduler

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        // Schedule subscription checks
        subscriptionScheduler.scheduleSubscriptionChecks()

        // Enable logging in debug
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Apply locale in background
        CoroutineScope(Dispatchers.Default).launch {
            LocaleHelper.applyLocale(this@ResbApplication)
        }
    }

    companion object {
        lateinit var sharedPreferences: SharedPreferences
            private set
    }
}
