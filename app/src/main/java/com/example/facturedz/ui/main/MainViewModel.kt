package com.example.facturedz.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturedz.domain.model.InvoiceDomain
import com.example.facturedz.domain.usecase.GenerateDashboardDataUseCase
import com.example.facturedz.domain.usecase.GetInvoicesUseCase
import com.example.facturedz.domain.usecase.SearchInvoicesUseCase
import com.example.facturedz.domain.usecase.DashboardData // Assuming DashboardData is in usecase package
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModel for the main screen (Invoice List and Dashboard)
class MainViewModel(
    private val getInvoicesUseCase: GetInvoicesUseCase, // To be injected
    private val searchInvoicesUseCase: SearchInvoicesUseCase, // To be injected
    private val generateDashboardDataUseCase: GenerateDashboardDataUseCase // To be injected
    // Add SharedPreferences or a SettingsRepository to get the suspiciousAmountThreshold
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _allInvoices = getInvoicesUseCase()

    val invoices: StateFlow<List<InvoiceDomain>> = searchQuery
        .combine(_allInvoices) { query, invoices ->
            if (query.isBlank()) {
                invoices
            } else {
                // This search logic might be better inside the UseCase/Repository if complex
                invoices.filter {
                    it.client?.name?.contains(query, ignoreCase = true) == true ||
                    it.invoiceNumber?.contains(query, ignoreCase = true) == true ||
                    it.totalTTC.toString().contains(query, ignoreCase = true)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _dashboardData = MutableStateFlow<DashboardDataUiState>(DashboardDataUiState.Loading)
    val dashboardData: StateFlow<DashboardDataUiState> = _dashboardData.asStateFlow()

    init {
        loadDashboardData()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _dashboardData.value = DashboardDataUiState.Loading
            // Fetch suspiciousAmountThreshold from settings repository/SharedPreferences
            val suspiciousAmountThreshold = 1000000.0 // Placeholder
            generateDashboardDataUseCase(suspiciousAmountThreshold).collect {
                data -> _dashboardData.value = DashboardDataUiState.Success(data)
            }
        }
    }
}

sealed interface InvoiceListUiState {
    data class Success(val invoices: List<InvoiceDomain>) : InvoiceListUiState
    data class Error(val message: String) : InvoiceListUiState
    object Loading : InvoiceListUiState
}

sealed interface DashboardDataUiState {
    data class Success(val data: DashboardData) : DashboardDataUiState
    data class Error(val message: String) : DashboardDataUiState
    object Loading : DashboardDataUiState
}
