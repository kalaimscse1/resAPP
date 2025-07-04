package com.warriortech.resb.di



import android.content.Context
import androidx.room.Room
import com.warriortech.resb.data.local.RestaurantDatabase
import com.warriortech.resb.data.local.dao.MenuItemDao
import com.warriortech.resb.data.local.dao.TableDao
import com.warriortech.resb.data.repository.DashboardRepository
import com.warriortech.resb.data.repository.MenuItemRepository
import com.warriortech.resb.data.repository.OrderRepository
import com.warriortech.resb.data.repository.SettingsRepository
import com.warriortech.resb.data.repository.TableRepository
import com.warriortech.resb.data.sync.SyncManager
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRestaurantDatabase(@ApplicationContext context: Context): RestaurantDatabase {
        return Room.databaseBuilder(
            context,
            RestaurantDatabase::class.java,
            "kts-resb"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTableDao(database: RestaurantDatabase): TableDao {
        return database.tableDao()
    }
//
    @Provides
    @Singleton
    fun provideMenuItemDao(database: RestaurantDatabase): MenuItemDao {
        return database.menuItemDao()
    }

//    @Provides
//    @Singleton
//    fun provideOrderDao(database: RestaurantDatabase): OrderDao {
//        return database.orderDao()
//    }

//    @Provides
//    @Singleton
//    fun provideOrderItemDao(database: RestaurantDatabase): OrderItemDao {
//        return database.orderItemDao()
//    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.6:5050/api/") // Replace with your actual API base URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTableRepository(
        tableDao: TableDao,
        apiService: ApiService,
        networkMonitor: NetworkMonitor
    ): TableRepository {
        return TableRepository(tableDao, apiService, networkMonitor)
    }

    @Provides
    @Singleton
    fun provideMenuItemRepository(
        menuItemDao: MenuItemDao,
        apiService: ApiService,
        networkMonitor: NetworkMonitor
    ): MenuItemRepository {
        return MenuItemRepository(
            menuItemDao, apiService,
            networkMonitor
        )
    }

    @Provides
    @Singleton
    fun provideOrderRepository(
        apiService: ApiService,
    ): OrderRepository {
        return OrderRepository( apiService)
    }

    @Provides
    @Singleton
    fun dashboardRepository(
        apiService: ApiService
    ): DashboardRepository {
        return DashboardRepository(
            apiService
        )
    }

    @Provides
    @Singleton
    fun settingRepository(
        apiService: ApiService
    ): SettingsRepository {
        return SettingsRepository(
            apiService
        )
    }

    @Provides
    @Singleton
    fun provideSyncManager(
        @ApplicationContext context: Context,
        networkMonitor: NetworkMonitor,
        apiService: ApiService
    ): SyncManager {
        return SyncManager(context, networkMonitor, apiService)
    }
}