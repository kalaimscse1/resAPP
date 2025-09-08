package com.warriortech.resb.data.sync

import android.content.Context
import androidx.work.*
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.util.ConnectionState
import com.warriortech.resb.util.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val context: Context,
    private val networkMonitor: NetworkMonitor,
    private val workManager: WorkManager
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        observeNetworkConnectivity()
    }

    private fun observeNetworkConnectivity() {
        coroutineScope.launch {
            networkMonitor.isOnline
                .map { it == ConnectionState.Available }
                .distinctUntilChanged()
                .collect { isOnline ->
                    if (isOnline) {
                        scheduleSyncWork()
                    } else {
                        workManager.cancelUniqueWork(SyncWorker.WORK_NAME)
                    }
                }
        }
    }

    fun scheduleSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            SyncWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "${SyncWorker.WORK_NAME}_PERIODIC",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )
    }

    fun cancelSync() {
        workManager.cancelUniqueWork(SyncWorker.WORK_NAME)
    }
}
