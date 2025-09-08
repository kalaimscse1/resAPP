package com.warriortech.resb

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.warriortech.resb.data.sync.SyncWorker
import com.warriortech.resb.network.SessionManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.BuildConfig
import timber.log.Timber
import javax.inject.Inject
import com.warriortech.resb.util.LocaleHelper
import com.warriortech.resb.util.SubscriptionScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class ResbApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var subscriptionScheduler: SubscriptionScheduler
    @Inject
    lateinit var workerFactory: androidx.hilt.work.HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        subscriptionScheduler.scheduleSubscriptionChecks()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        CoroutineScope(Dispatchers.Default).launch {
            LocaleHelper.applyLocale(this@ResbApplication)
        }
    }

    companion object {
        lateinit var sharedPreferences: SharedPreferences
            private set
    }
}

