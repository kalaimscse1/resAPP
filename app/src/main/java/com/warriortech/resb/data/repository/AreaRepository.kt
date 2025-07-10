
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.Area
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AreaRepository @Inject constructor() {
    private val _areas = MutableStateFlow<List<Area>>(emptyList())
    val areas: Flow<List<Area>> = _areas.asStateFlow()

    init {
        // Initialize with sample data
        _areas.value = listOf(
            Area(1, "Main Hall", "Primary dining area"),
            Area(2, "VIP Section", "Private dining area"),
            Area(3, "Terrace", "Outdoor seating area")
        )
    }

    suspend fun getAllAreas(): List<Area> {
        return _areas.value
    }

    suspend fun addArea(area: Area) {
        val newId = (_areas.value.maxOfOrNull { it.id } ?: 0) + 1
        val newArea = area.copy(id = newId)
        _areas.value = _areas.value + newArea
    }

    suspend fun updateArea(area: Area) {
        _areas.value = _areas.value.map { if (it.id == area.id) area else it }
    }

    suspend fun deleteArea(areaId: Long) {
        _areas.value = _areas.value.filter { it.id != areaId }
    }
}
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.Area
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AreaRepository @Inject constructor(
    // Add your data source here (Room DAO, API service, etc.)
) {
    
    suspend fun getAllAreas(): List<Area> {
        // TODO: Implement actual data fetching
        // For now, return sample data
        return listOf(
            Area(1, "Main Hall", "Primary dining area"),
            Area(2, "Terrace", "Outdoor seating area"),
            Area(3, "Private Room", "VIP dining area")
        )
    }

    suspend fun insertArea(area: Area) {
        // TODO: Implement actual data insertion
    }

    suspend fun updateArea(area: Area) {
        // TODO: Implement actual data update
    }

    suspend fun deleteArea(areaId: Int) {
        // TODO: Implement actual data deletion
    }
}
