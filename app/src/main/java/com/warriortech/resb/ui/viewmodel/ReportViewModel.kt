package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.ReportRepository
import com.warriortech.resb.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.fold

sealed interface ReportUiState {
    object Loading : ReportUiState
    data class Success(
        val todaySales: TodaySalesReport?,
        val gstSummary: GSTSummaryReport?,
        val salesSummary: SalesSummaryReport?
    ) : ReportUiState
    data class Error(val message: String) : ReportUiState
}

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Loading)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        loadReports()
    }

    private fun loadReports() {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading
            try {
                var todaySales: TodaySalesReport? = null
                var gstSummary: GSTSummaryReport? = null
                var salesSummary: SalesSummaryReport? = null

                // Fetch today's sales
                reportRepository.getTodaySales().collect { result ->
                    result.fold(
                        onSuccess = { data -> todaySales = data },
                        onFailure = { /* Log error but continue */ }
                    )
                }

                // Fetch GST summary
                reportRepository.getGSTSummary().collect { result ->
                    result.fold(
                        onSuccess = { data -> gstSummary = data },
                        onFailure = { /* Log error but continue */ }
                    )
                }

                // Fetch sales summary for today
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                reportRepository.getSalesSummaryByDate(today).collect { result ->
                    result.fold(
                        onSuccess = { data -> salesSummary = data },
                        onFailure = { /* Log error but continue */ }
                    )
                }

                _uiState.value = ReportUiState.Success(
                    todaySales = todaySales,
                    gstSummary = gstSummary,
                    salesSummary = salesSummary
                )
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error("Failed to load reports: ${e.message}")
            }
        }
    }

    fun refreshReports() {
        loadReports()
    }

    fun loadReportsForDate(date: String) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading
            try {
                var todaySales: TodaySalesReport? = null
                var gstSummary: GSTSummaryReport? = null
                var salesSummary: SalesSummaryReport? = null

                // Keep today's sales as is for now (could be modified to fetch for specific date)
                reportRepository.getTodaySales().collect { result ->
                    result.fold(
                        onSuccess = { data -> todaySales = data },
                        onFailure = { /* Log error but continue */ }
                    )
                }

                // Keep GST summary as is
                reportRepository.getGSTSummary().collect { result ->
                    result.fold(
                        onSuccess = { data -> gstSummary = data },
                        onFailure = { /* Log error but continue */ }
                    )
                }

                // Fetch sales summary for specific date
                reportRepository.getSalesSummaryByDate(date).collect { result ->
                    result.fold(
                        onSuccess = { data -> salesSummary = data },
                        onFailure = { /* Log error but continue */ }
                    )
                }

                _uiState.value = ReportUiState.Success(
                    todaySales = todaySales,
                    gstSummary = gstSummary,
                    salesSummary = salesSummary
                )
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error("Failed to load reports for $date: ${e.message}")
            }
        }
    }
}