package com.example.facturedz.ui.addeditinvoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturedz.data.repository.InvoiceRepository // Placeholder
import com.example.facturedz.domain.model.InvoiceDomain
import com.example.facturedz.domain.usecase.AddInvoiceUseCase
import com.example.facturedz.domain.usecase.DetectAnomaliesUseCase
import com.example.facturedz.domain.usecase.GetInvoiceDetailsUseCase
import com.example.facturedz.ocr.OcrResult // Placeholder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Simplified ViewModel for Add/Edit Invoice screen
class AddEditInvoiceViewModel(
    private val invoiceRepository: InvoiceRepository, // To be injected
    private val addInvoiceUseCase: AddInvoiceUseCase, // To be injected
    private val getInvoiceDetailsUseCase: GetInvoiceDetailsUseCase, // To be injected
    private val detectAnomaliesUseCase: DetectAnomaliesUseCase // To be injected
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddEditInvoiceUiState>(AddEditInvoiceUiState.Loading)
    val uiState: StateFlow<AddEditInvoiceUiState> = _uiState.asStateFlow()

    private val _currentInvoice = MutableStateFlow<InvoiceDomain?>(null)
    val currentInvoice: StateFlow<InvoiceDomain?> = _currentInvoice.asStateFlow()

    fun loadInvoice(invoiceId: Long) {
        viewModelScope.launch {
            if (invoiceId == 0L) { // New invoice
                _currentInvoice.value = InvoiceDomain(date = System.currentTimeMillis()) // Default new invoice
                _uiState.value = AddEditInvoiceUiState.Success(_currentInvoice.value!!)
            } else {
                getInvoiceDetailsUseCase(invoiceId).collect {
                    invoice ->
                        _currentInvoice.value = invoice
                        _uiState.value = if (invoice != null) AddEditInvoiceUiState.Success(invoice) else AddEditInvoiceUiState.Error("Facture non trouvée")
                }
            }
        }
    }

    fun saveInvoice(invoice: InvoiceDomain) {
        viewModelScope.launch {
            // Perform anomaly detection before saving
            // val anomalies = detectAnomaliesUseCase(invoice, 1000000.0) // Threshold from settings
            // val invoiceWithAnomalies = invoice.copy(anomalies = anomalies)
            
            val newInvoiceId = addInvoiceUseCase(invoice) // Save and get ID
            // Handle navigation or success message
        }
    }

    fun processOcrResult(ocrResult: OcrResult) {
        // Convert OcrResult to InvoiceDomain fields
        // Update _currentInvoice.value
        // This is a complex step involving parsing and validation
        val updatedInvoice = _currentInvoice.value?.copy(
            // Example: client = ClientDomain(name = ocrResult.clientName ?: ""),
            // totalTTC = ocrResult.totalTTC?.toDoubleOrNull() ?: 0.0
            // ... and so on for all fields and product lines
        ) ?: InvoiceDomain(date = System.currentTimeMillis())
        _currentInvoice.value = updatedInvoice
        _uiState.value = AddEditInvoiceUiState.Success(updatedInvoice)
    }
}

sealed interface AddEditInvoiceUiState {
    data class Success(val invoice: InvoiceDomain) : AddEditInvoiceUiState
    data class Error(val message: String) : AddEditInvoiceUiState
    object Loading : AddEditInvoiceUiState
}
