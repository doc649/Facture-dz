package com.example.facturedz.domain.usecase

import com.example.facturedz.data.repository.InvoiceRepository
import com.example.facturedz.domain.model.InvoiceDomain
import com.example.facturedz.domain.model.ProductLineDomain
import com.example.facturedz.domain.model.toEntity
import com.example.facturedz.domain.model.toInvoiceEntity

class AddInvoiceUseCase(private val repository: InvoiceRepository) {

    suspend operator fun invoke(invoiceDomain: InvoiceDomain): Long {
        // 1. Save the client if it's new or get existing client ID
        // This logic might be more complex, e.g., allowing selection from existing clients
        // or creating a new one. For now, assume client in invoiceDomain is to be created/updated.
        val clientId = invoiceDomain.client?.let {
            // A real app might check if client exists by name/NIF/RC to avoid duplicates
            // or provide UI to link to existing client.
            repository.insertClient(it.toEntity())
        }

        // 2. Create the main invoice entity
        val invoiceEntity = invoiceDomain.toInvoiceEntity().copy(clientId = clientId)
        val invoiceId = repository.insertInvoice(invoiceEntity)

        // 3. Save product lines associated with this invoice
        if (invoiceDomain.productLines.isNotEmpty()) {
            val productLineEntities = invoiceDomain.productLines.map {
                it.toEntity().copy(invoiceId = invoiceId)
            }
            repository.insertProductLines(productLineEntities)
        }

        // 4. Anomaly detection would typically be called after this
        // For now, just returning the invoiceId
        return invoiceId
    }
}
