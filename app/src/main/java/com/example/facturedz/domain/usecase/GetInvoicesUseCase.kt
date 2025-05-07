package com.example.facturedz.domain.usecase

import com.example.facturedz.data.repository.InvoiceRepository
import com.example.facturedz.domain.model.InvoiceDomain
import com.example.facturedz.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetInvoicesUseCase(private val repository: InvoiceRepository) {
    operator fun invoke(): Flow<List<InvoiceDomain>> {
        return repository.getAllInvoicesWithDetails().map {
            list -> list.map { it.toDomain() }
        }
    }
}

class GetInvoiceDetailsUseCase(private val repository: InvoiceRepository) {
    operator fun invoke(invoiceId: Long): Flow<InvoiceDomain?> {
        return repository.getInvoiceWithDetailsById(invoiceId).map {
            it?.toDomain()
        }
    }
}

class SearchInvoicesUseCase(private val repository: InvoiceRepository) {
    operator fun invoke(query: String): Flow<List<InvoiceDomain>> {
        return repository.searchInvoices(query).map {
            list -> list.map { it.toDomain() }
        }
    }
}
