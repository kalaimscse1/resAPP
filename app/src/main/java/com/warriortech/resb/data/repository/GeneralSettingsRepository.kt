
package com.warriortech.resb.data.repository

import com.warriortech.resb.data.api.ApiService
import com.warriortech.resb.model.GeneralSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralSettingsRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getGeneralSettings(): GeneralSettings? {
        return try {
            apiService.getGeneralSettings()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateGeneralSettings(settings: GeneralSettings): GeneralSettings? {
        return try {
            apiService.updateGeneralSettings(settings)
        } catch (e: Exception) {
            null
        }
    }
}
