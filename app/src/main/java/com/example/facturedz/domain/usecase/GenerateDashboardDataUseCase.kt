package com.example.facturedz.domain.usecase

import com.example.facturedz.data.repository.IInvoiceRepository // Use interface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class DashboardData(
    val totalInvoicedThisMonth: Double = 0.0,
    val frequentClients: List<String> = emptyList(),
    val detectedAnomaliesCount: Int = 0,
    val invoicesToWatchCount: Int = 0
)

class GenerateDashboardDataUseCase(private val repository: IInvoiceRepository) {

    operator fun invoke(suspiciousAmountThreshold: Double): Flow<DashboardData> {
        val totalInvoicedThisMonthFlow: Flow<Double> = repository.getTotalInvoicedForCurrentMonth()
        val frequentClientsFlow: Flow<List<String>> = repository.getMostFrequentClients(limit = 3) // Get top 3 clients
        val detectedAnomaliesCountFlow: Flow<Int> = repository.getDetectedAnomaliesCount()
        val invoicesToWatchCountFlow: Flow<Int> = repository.getInvoicesAboveThreshold(suspiciousAmountThreshold)
            .combine(repository.getInvoices()) { suspicious, allInvoices -> // Combine to count only those marked as suspect
                suspicious.count { it.isSuspect } // Or based on anomaly type if more specific
            }

        return combine(
            totalInvoicedThisMonthFlow,
            frequentClientsFlow,
            detectedAnomaliesCountFlow,
            invoicesToWatchCountFlow
        ) { totalMonth, clients, anomalies, toWatch ->
            DashboardData(
                totalInvoicedThisMonth = totalMonth,
                frequentClients = clients,
                detectedAnomaliesCount = anomalies,
                invoicesToWatchCount = toWatch
            )
        }
    }
}
