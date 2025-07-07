
package com.warriortech.resb.ui.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.DashboardRepository
import com.warriortech.resb.data.repository.OrderRepository
import com.warriortech.resb.model.DashboardMetrics
import com.warriortech.resb.model.RunningOrder
import com.warriortech.resb.model.TblOrderDetailsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    val tblOrderDetailsResponse = MutableStateFlow<List<TblOrderDetailsResponse>>(emptyList())


    sealed class UiState {
        object Loading : UiState()
        data class Success(
            val metrics: DashboardMetrics,
            val runningOrders: List<RunningOrder>,
            val recentActivity: List<String>
        ) : UiState()
        data class Error(val message: String) : UiState()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val metrics = dashboardRepository.getDashboardMetrics()
                val runningOrders = dashboardRepository.getRunningOrders()
                val recentActivity = dashboardRepository.getRecentActivity()

                _uiState.value = UiState.Success(
                    metrics = metrics,
                    runningOrders = runningOrders,
                    recentActivity = recentActivity
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load dashboard: ${e.message}")
            }
        }
    }

    fun refreshDashboard() {
        loadDashboardData()
    }

    @SuppressLint("SuspiciousIndentation")
    fun getOrdersByOrderId(lng: Long): List<TblOrderDetailsResponse> {
        viewModelScope.launch {
            val order = orderRepository.getOrdersByOrderId(lng)
            if (order.body()!=null)
                tblOrderDetailsResponse.value = order.body()!!
            return@launch
        }
        return emptyList()
    }
}
