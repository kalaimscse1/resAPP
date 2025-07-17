package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.ReportRepository
import com.warriortech.resb.model.*
import com.warriortech.resb.util.ExportUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.fold
import android.content.Context


@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Loading)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    sealed interface ReportUiState {
        object Loading : ReportUiState
        data class Success(
            val todaySales: TodaySalesReport?,
            val gstSummary: GSTSummaryReport?,
            val salesSummary: SalesSummaryReport?
        ) : ReportUiState
        data class Error(val message: String) : ReportUiState
    }

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
                        onFailure = { ReportUiState.Error("Failed to load reports: ") }
                    )
                }

                // Fetch GST summary
                reportRepository.getGSTSummary().collect { result ->
                    result.fold(
                        onSuccess = { data -> gstSummary = data },
                        onFailure = { ReportUiState.Error("Failed to load reports: ") }
                    )
                }

                // Fetch sales summary for today
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                reportRepository.getSalesSummaryByDate(today).collect { result ->
                    result.fold(
                        onSuccess = { data -> salesSummary = data },
                        onFailure = { ReportUiState.Error("Failed to load reports: ") }
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
                        onFailure = { ReportUiState.Error("Failed to load reports for $date: ") }
                    )
                }

                // Keep GST summary as is
                reportRepository.getGSTSummary().collect { result ->
                    result.fold(
                        onSuccess = { data -> gstSummary = data },
                        onFailure = { ReportUiState.Error("Failed to load reports for $date: ") }
                    )
                }

                // Fetch sales summary for specific date
                reportRepository.getSalesSummaryByDate(date).collect { result ->
                    result.fold(
                        onSuccess = { data -> salesSummary = data },
                        onFailure = { ReportUiState.Error("Failed to load reports for $date: ") }
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

    fun exportToPDF(selectedDate: String, context: Context) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is ReportUiState.Success) {
                val file = ExportUtils.exportToPDF(
                    context = context,
                    todaySales = currentState.todaySales,
                    gstSummary = currentState.gstSummary,
                    salesSummary = currentState.salesSummary,
                    selectedDate = selectedDate
                )
                file?.let {
                    ExportUtils.shareFile(context, it)
                }
            }
        }
    }

    fun exportToExcel(selectedDate: String, context: Context) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is ReportUiState.Success) {
                val file = ExportUtils.exportToExcel(
                    context = context,
                    todaySales = currentState.todaySales,
                    gstSummary = currentState.gstSummary,
                    salesSummary = currentState.salesSummary,
                    selectedDate = selectedDate
                )
                file?.let {
                    ExportUtils.shareFile(context, it)
                }
            }
        }
    }
}