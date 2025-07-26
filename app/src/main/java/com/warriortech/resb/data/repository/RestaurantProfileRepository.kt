package com.warriortech.resb.data.repository

import com.warriortech.resb.model.RestaurantProfile
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantProfileRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getRestaurantProfile(): RestaurantProfile? {
        return try {
            apiService.getRestaurantProfile(SessionManager.getCompanyCode()?:"",SessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateRestaurantProfile(profile: RestaurantProfile): RestaurantProfile? {
        return try {
            apiService.updateRestaurantProfile(SessionManager.getCompanyCode()?:"",profile,SessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            null
        }
    }
}
