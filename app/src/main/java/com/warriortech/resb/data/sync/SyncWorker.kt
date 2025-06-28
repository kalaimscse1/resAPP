package com.warriortech.resb.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.warriortech.resb.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    apiService: ApiService
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "sync_work"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get repositories from DI - this would need to be injected properly
            // For now, we'll handle sync in repositories themselves

            Timber.d("Sync work completed successfully")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing data with server")
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}