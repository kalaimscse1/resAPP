import com.warriortech.resb.data.repository.MenuRepository
import com.warriortech.resb.data.repository.ModifierRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.persistence.MenuDao
import com.warriortech.resb.utils.NetworkMonitor
import com.warriortech.resb.utils.SessionManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMenuRepository(
        apiService: ApiService,
        menuDao: MenuDao,
        sessionManager: SessionManager,
        networkMonitor: NetworkMonitor
    ): MenuRepository = MenuRepository(apiService, menuDao, sessionManager, networkMonitor)

    @Provides
    @Singleton
    fun provideModifierRepository(
        apiService: ApiService,
        sessionManager: SessionManager
    ): ModifierRepository = ModifierRepository(apiService, sessionManager)
}