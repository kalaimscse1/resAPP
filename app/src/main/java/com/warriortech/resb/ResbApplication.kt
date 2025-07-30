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
import com.warriortech.resb.network.RetrofitClient.apiService
import com.warriortech.resb.network.SessionManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.BuildConfig
import timber.log.Timber
import javax.inject.Inject
import com.warriortech.resb.util.LocaleHelper

@HiltAndroidApp
class ResbApplication : Application(), Configuration.Provider {
    lateinit var sessionManager: SessionManager
    companion object {
        lateinit var sharedPreferences: SharedPreferences
            private set // Make setter private to ensure it's only set here
    }
    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging
        sessionManager = SessionManager(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
    override fun getWorkManagerConfiguration(): Configuration {
        val syncWorkerFactory = object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
            ): ListenableWorker? {
                return if (workerClassName == SyncWorker::class.java.name) {
                    SyncWorker(
                        appContext,
                        workerParameters,
                        apiService,
                        sessionManager
                    )
                } else {
                    null
                }
            }
        }
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .setWorkerFactory(syncWorkerFactory)
            .build()
    }
    private fun isDebugBuild(): Boolean {
        return applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(base ?: this))
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleHelper.onAttach(this)
    }
}

//class CustomWorkerFactory @Inject constructor() : WorkerFactory() {
//    val sessionManager = SessionManager(this)
//    override fun createWorker(
//        appContext: Context,
//        workerClassName: String,
//        workerParameters: WorkerParameters
//    ): ListenableWorker? {
//        return when (workerClassName) {
//            SyncWorker::class.java.name -> SyncWorker(
//                appContext,
//                workerParameters,
//                apiService,
//                sessionManager
//            )
//            else -> null
//        }
//    }
//}