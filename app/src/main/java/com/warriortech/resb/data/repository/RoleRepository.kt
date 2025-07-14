
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.Role
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoleRepository @Inject constructor() {
    private val _roles = MutableStateFlow<List<Role>>(emptyList())
    val roles: Flow<List<Role>> = _roles.asStateFlow()

    init {
        // Initialize with sample data
        _roles.value = listOf(
            Role(1, "Admin", 1),
            Role(2, "Manager", 1),
            Role(3, "Waiter", 1),
            Role(4, "Chef", 1)
        )
    }

    suspend fun getAllRoles(): List<Role> {
        return _roles.value
    }

    suspend fun addRole(role: Role) {
        val newId = (_roles.value.maxOfOrNull { it.role_id } ?: 0) + 1
        val newRole = role.copy(role_id = newId)
        _roles.value = _roles.value + newRole
    }

    suspend fun updateRole(role: Role) {
        _roles.value = _roles.value.map { if (it.role_id == role.role_id) role else it }
    }

    suspend fun deleteRole(roleId: Long) {
        _roles.value = _roles.value.filter { it.role_id != roleId }
    }
}
package com.warriortech.resb.data.repository

import com.warriortech.resb.data.api.ApiService
import com.warriortech.resb.model.Role
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoleRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllRoles(): List<Role> {
        return try {
            apiService.getRoles()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRoleById(id: Int): Role? {
        return try {
            apiService.getRoleById(id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createRole(role: Role): Role? {
        return try {
            apiService.createRole(role)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateRole(role: Role): Role? {
        return try {
            apiService.updateRole(role.id, role)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteRole(id: Int): Boolean {
        return try {
            apiService.deleteRole(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}
