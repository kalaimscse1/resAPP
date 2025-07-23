
package com.warriortech.resb.data.repository

import com.warriortech.resb.data.api.ApiService
import com.warriortech.resb.model.RegistrationRequest
import com.warriortech.resb.model.RegistrationResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun registerCompany(registrationRequest: RegistrationRequest): RegistrationResponse {
        return try {
            apiService.registerCompany(registrationRequest)
        } catch (e: Exception) {
            RegistrationResponse(
                success = false,
                message = "Registration failed: ${e.message}",
                data = null
            )
        }
    }
}
