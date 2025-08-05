package com.warriortech.resb.data.repository

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.warriortech.resb.model.RestaurantProfile
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantProfileRepository @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    suspend fun getRestaurantProfile(): RestaurantProfile? {
        return try {
            apiService.getRestaurantProfile(sessionManager.getCompanyCode()?:"",sessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateRestaurantProfile(profile: RestaurantProfile): RestaurantProfile? {
        return try {
            apiService.updateRestaurantProfile(sessionManager.getCompanyCode()?:"",profile,sessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun uploadImageToServer(uri: Uri, context: Context, companyCode: String) : Flow<Result<String>>  =
        flow{
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return@flow
            val file = File(context.cacheDir, "upload_image")
            file.outputStream().use { inputStream.copyTo(it) }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val companyCodeBody = companyCode.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.uploadLogo(companyCode,multipartBody,companyCode)

            if (response.isSuccessful) {
                Result.success("Image uploaded successfully")
            } else {
               Result.failure(Exception("Image upload failed"))
            }
        }
}
