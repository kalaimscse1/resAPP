package com.warriortech.resb.data.repository

import com.warriortech.resb.model.TblGroupDetails
import com.warriortech.resb.model.TblGroupNature
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import okhttp3.ResponseBody
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {

    suspend fun getGroups(): List<TblGroupDetails>? {
        return try {
            apiService.getAllGroups(sessionManager.getCompanyCode()?:"").body()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getGroupById(groupId: Int): TblGroupDetails? {
        return try {
            apiService.getGroupById(groupId, sessionManager.getCompanyCode()?:"").body()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createGroup(group: TblGroupDetails): TblGroupDetails? {
        return try {
            apiService.createGroup(group, sessionManager.getCompanyCode()?:"").body()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateGroup(groupId: Long, group: TblGroupDetails): Int? {
        return try {
            apiService.updateGroup(groupId, group, sessionManager.getCompanyCode()?:"").body()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteGroup(groupId: Long): ResponseBody? {
        return try {
            apiService.deleteGroup(groupId, sessionManager.getCompanyCode()?:"").body()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getGroupNatures():List<TblGroupNature>?{
        return try {
            apiService.getGroupNatures(sessionManager.getCompanyCode()?:"").body()
        } catch (e: Exception) {
            null
        }
    }
}