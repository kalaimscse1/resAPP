
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
            Area(1, "Main Hall", true),
            Area(2, "VIP Section", true),
            Area(3, "Terrace", true)
        )
    }

    suspend fun getAllAreas(): List<Area> {
        return _areas.value
    }

    suspend fun addArea(area: Area) {
        val newId = (_areas.value.maxOfOrNull { it.area_id } ?: 0) + 1
        val newArea = area.copy(area_id =  newId)
        _areas.value = _areas.value + newArea
    }

    suspend fun updateArea(area: Area) {
        _areas.value = _areas.value.map { if (it.area_id == area.area_id) area else it }
    }

    suspend fun deleteArea(areaId: Long) {
        _areas.value = _areas.value.filter { it.area_id != areaId }
    }
}

