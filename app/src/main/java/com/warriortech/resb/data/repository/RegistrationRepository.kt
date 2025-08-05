package com.warriortech.resb.data.repository

import android.annotation.SuppressLint
import com.warriortech.resb.model.RegistrationRequest
import com.warriortech.resb.model.RegistrationResponse
import com.warriortech.resb.model.RestaurantProfile
import com.warriortech.resb.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationRepository @Inject constructor(
    private val apiService: ApiService
) {
    @SuppressLint("SuspiciousIndentation")
    fun registerCompany(registrationRequest: RegistrationRequest): Flow<Result<RestaurantProfile>> = flow {
        try {
            val response = apiService.registerCompany(registrationRequest)

            if (response.isSuccessful && response.body()?.data != null) {
                val res = response.body()!!.data!!

                val profile = RestaurantProfile(
                    company_code = res.company_master_code,
                    company_name = res.company_name,
                    owner_name = res.owner_name,
                    address1 = res.address1,
                    address2 = res.address2,
                    place = res.place,
                    pincode = res.pincode,
                    contact_no = res.contact_no,
                    mail_id = res.mail_id,
                    country = res.country,
                    state = res.state,
                    currency = "Rs",
                    tax_no = "",
                    decimal_point = 2L
                )

                val result = apiService.addRestaurantProfile(profile, profile.company_code)

                if (result.isSuccessful && result.body() != null) {
                    emit(Result.success(result.body()!!))
                } else {
                    emit(Result.failure(Exception("Profile creation failed: ${result.message()}")))
                }

            } else {
                emit(Result.failure(Exception("Company registration failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception("Error occurred: ${e.localizedMessage}")))
        }
    }

    suspend fun getCompanyCode(): Map<String, String>{
        return apiService.getCompanyCode()
    }

//    suspend fun addRestaurantProfile(profile: RestaurantProfile) :RestaurantProfile?{
//        return apiService.addRestaurantProfile(profile,profile.company_code)
//    }
}
