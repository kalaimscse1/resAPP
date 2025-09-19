package com.warriortech.resb.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.warriortech.resb.data.sync.SyncWorker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerModule {

    @Binds
    @IntoMap
    @StringKey("com.warriortech.resb.data.sync.SyncWorker")
    abstract fun bindSyncWorker(factory: SyncWorker.AssistedFactory): ChildWorkerFactory
}

interface ChildWorkerFactory {
    fun create(appContext: Context, workerParameters: WorkerParameters): ListenableWorker
}

@Singleton
class HiltWorkerFactory @Inject constructor(
    private val workerFactories: Map<String, @JvmSuppressWildcards Provider<ChildWorkerFactory>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val foundEntry = workerFactories.entries.find { 
            Class.forName(workerClassName).isAssignableFrom(Class.forName(it.key)) 
        }
        val factoryProvider = foundEntry?.value
            ?: workerFactories[workerClassName]
            ?: return null
        return factoryProvider.get().create(appContext, workerParameters)
    }
}