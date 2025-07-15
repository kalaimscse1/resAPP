package com.warriortech.resb.di



import android.content.Context
import androidx.room.Room
import com.warriortech.resb.ai.AIRepository
import com.warriortech.resb.data.local.RestaurantDatabase
import com.warriortech.resb.data.local.dao.MenuItemDao
import com.warriortech.resb.data.local.dao.TableDao
import com.warriortech.resb.data.repository.AreaRepository
import com.warriortech.resb.data.repository.DashboardRepository
import com.warriortech.resb.data.repository.MenuItemRepository
import com.warriortech.resb.data.repository.OrderRepository
import com.warriortech.resb.data.repository.SettingsRepository
import com.warriortech.resb.data.repository.TableRepository
import com.warriortech.resb.data.repository.CounterRepository
import com.warriortech.resb.data.repository.GeneralSettingsRepository
import com.warriortech.resb.data.repository.PrinterRepository
import com.warriortech.resb.data.repository.RestaurantProfileRepository
import com.warriortech.resb.data.repository.RoleRepository
import com.warriortech.resb.data.repository.StaffRepository
import com.warriortech.resb.data.repository.TaxRepository
import com.warriortech.resb.data.repository.TaxSplitRepository
import com.warriortech.resb.data.repository.TemplateRepository
import com.warriortech.resb.data.repository.VoucherRepository
import com.warriortech.resb.data.sync.SyncManager
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.service.PrintService
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
    fun provideAIRepository(@ApplicationContext context: Context): AIRepository {
        return AIRepository(context)
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
        return OrderRepository(apiService)
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

    @Provides
    @Singleton
    fun provideCounterRepository(
        apiService: ApiService
    ): CounterRepository {
        return CounterRepository(
            apiService = apiService
        )
    }

    @Provides
    @Singleton
    fun provideAreaRepository(
        apiService: ApiService
    ): AreaRepository {
        return AreaRepository(
            apiService = apiService
        )
    }

    @Provides
    @Singleton
    fun provideStaffRepository(
        apiService: ApiService
    ): StaffRepository {
        return StaffRepository(
            apiService = apiService
        )
    }

    @Provides
    @Singleton
    fun provideRoleRepository(
        apiService: ApiService
    ): RoleRepository {
        return RoleRepository(
            apiService = apiService
        )
    }

    @Provides
    @Singleton
    fun providePrinterRepository(
        apiService: ApiService
    ): PrinterRepository {
        return PrinterRepository(
            apiService = apiService
        )
    }

    @Provides
    @Singleton
    fun provideTemplateRepository(): TemplateRepository {
        return TemplateRepository()
    }

    @Provides
    @Singleton
    fun providePrintService(
        @ApplicationContext context: Context,
        templateRepository: TemplateRepository
    ): PrintService {
        return PrintService(context, templateRepository)
    }

    @Provides
    @Singleton
    fun provideTaxRepository(
        apiService: ApiService
    ): TaxRepository {
        return TaxRepository(
            apiService = apiService
        )
    }

    @Provides
    @Singleton
    fun provideTaxSplitRepository(
        apiService: ApiService
    ): TaxSplitRepository {
        return TaxSplitRepository(
            apiService = apiService
        )
    }

    @Provides
    @Singleton
    fun provideGeneralSettingsRepository(
        apiService: ApiService
    ): GeneralSettingsRepository {
        return GeneralSettingsRepository(
            apiService = apiService
        )
    }

    @Provides
    @Singleton
    fun provideRestaurantProfileRepository(
        apiService: ApiService
    ): RestaurantProfileRepository {
        return RestaurantProfileRepository(
            apiService = apiService
        )
    }

    @Provides
    @Singleton
    fun provideVoucherRepository(
        apiService: ApiService
    ): VoucherRepository {
        return VoucherRepository(
            apiService = apiService
        )
    }
}