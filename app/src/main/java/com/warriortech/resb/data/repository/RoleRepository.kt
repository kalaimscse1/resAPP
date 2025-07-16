
package com.warriortech.resb.data.repository

import com.warriortech.resb.network.ApiService
import com.warriortech.resb.model.Role
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoleRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllRoles(): List<Role> {
        return try {
            apiService.getRoles().body()!!
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRoleById(id: Int): Role? {
        return try {
            apiService.getRoleById(id).body()!!
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createRole(role: Role): Role? {
        return try {
            apiService.createRole(role).body()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateRole(role: Role): Int? {
        return try {
            apiService.updateRole(role.role_id, role).body()!!
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteRole(id: Long): Boolean {
        return try {
            apiService.deleteRole(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}
