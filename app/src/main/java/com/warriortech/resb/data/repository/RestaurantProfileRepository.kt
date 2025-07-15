
package com.warriortech.resb.data.repository


import com.warriortech.resb.model.RestaurantProfile
import com.warriortech.resb.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantProfileRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getRestaurantProfile(): RestaurantProfile? {
        return try {
            apiService.getRestaurantProfile()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateRestaurantProfile(profile: RestaurantProfile): RestaurantProfile? {
        return try {
            apiService.updateRestaurantProfile(profile)
        } catch (e: Exception) {
            null
        }
    }
}
