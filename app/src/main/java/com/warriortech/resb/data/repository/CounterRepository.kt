
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.Counter
import com.warriortech.resb.model.CounterSession
import com.warriortech.resb.model.Counters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CounterRepository @Inject constructor() {
    
    // Simulated data - replace with actual database implementation
    private val counters = listOf(
        Counters(1L, "Counter 1", "C1", "Main billing counter", true, "Ground Floor", "Staff 1"),
        Counters(2L, "Counter 2", "C2", "Express billing counter", true, "Ground Floor", "Staff 2"),
        Counters(3L, "Counter 3", "C3", "VIP billing counter", false, "First Floor", "Staff 3")
    )
    
    fun getActiveCounters(): Flow<Result<List<Counters>>> = flow {
        try {
            val activeCounters = counters.filter { it.isActive }
            emit(Result.success(activeCounters))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getAllCounters(): Flow<Result<List<Counters>>> = flow {
        try {
            emit(Result.success(counters))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getCounterById(id: Long): Flow<Result<Counters?>> = flow {
        try {
            val counter = counters.find { it.id == id }
            emit(Result.success(counter))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getCounterByCode(code: String): Flow<Result<Counters?>> = flow {
        try {
            val counter = counters.find { it.code == code }
            emit(Result.success(counter))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun createCounterSession(counterId: Long): Result<CounterSession> {
        return try {
            val counter = counters.find { it.id == counterId }
            if (counter != null) {
                val session = CounterSession(
                    counterId = counterId,
                    counterCode = counter.code,
                    sessionId = "SESSION_${System.currentTimeMillis()}",
                    startTime = System.currentTimeMillis()
                )
                Result.success(session)
            } else {
                Result.failure(Exception("Counter not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package com.warriortech.resb.data.repository

import com.warriortech.resb.data.api.ApiService
import com.warriortech.resb.model.Counter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CounterRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllCounters(): List<Counter> {
        return try {
            apiService.getCounters()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCounterById(id: Int): Counter? {
        return try {
            apiService.getCounterById(id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createCounter(counter: Counter): Counter? {
        return try {
            apiService.createCounter(counter)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateCounter(counter: Counter): Counter? {
        return try {
            apiService.updateCounter(counter.id, counter)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteCounter(id: Int): Boolean {
        return try {
            apiService.deleteCounter(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}
