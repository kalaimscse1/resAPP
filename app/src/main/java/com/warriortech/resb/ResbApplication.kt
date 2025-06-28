package com.warriortech.resb

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.warriortech.resb.data.sync.SyncManager
import com.warriortech.resb.data.sync.SyncWorker
import com.warriortech.resb.network.RetrofitClient.apiService
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.util.NetworkMonitor
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ResbApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var syncManager: SyncManager

//    @Inject
//    lateinit var syncWorkerFactory: SyncWorkerFactory

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    // Create database instance for the whole application
//    val database by lazy { RestaurantDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging
        if (isDebugBuild()) {
            Timber.plant(Timber.DebugTree())
        }

        // Initialize SessionManager
        SessionManager.init(this)

        // Initialize NetworkMonitor
//        networkMonitor.initialize()

        // Schedule periodic sync work
        setupSynchronization()
    }

    private fun setupSynchronization() {
        // Schedule periodic sync to run every 15 minutes when connected
        syncManager.schedulePeriodicSync()

        // Setup one-time sync when app starts if we're online
//        syncManager.performInitialSyncIfNeeded()
    }

    /**
     * Provide WorkManager configuration
     */
    override fun getWorkManagerConfiguration(): Configuration {
        val syncWorkerFactory = object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
            ): ListenableWorker? {
                return if (workerClassName == SyncWorker::class.java.name) {
                    SyncWorker(appContext, workerParameters, apiService)
                } else {
                    null
                }
            }
        }
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(syncWorkerFactory)
            .build()
    }

    /**
     * Helper method to check if app is in debug mode
     * without requiring BuildConfig
     */
    private fun isDebugBuild(): Boolean {
        return applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0
    }
}