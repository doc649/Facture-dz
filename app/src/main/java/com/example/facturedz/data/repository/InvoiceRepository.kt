package com.example.facturedz.data.repository

import com.example.facturedz.data.local.InvoiceDao
import com.example.facturedz.data.model.InvoiceWithDetails
import com.example.facturedz.domain.model.AnomalyDomain
import com.example.facturedz.domain.model.ClientDomain
import com.example.facturedz.domain.model.InvoiceDomain
import com.example.facturedz.domain.model.ProductLineDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

// Interface for InvoiceRepository (if not already defined, or to add new methods)
interface IInvoiceRepository {
    fun getInvoices(): Flow<List<InvoiceDomain>>
    fun getInvoiceById(id: Long): Flow<InvoiceDomain?>
    suspend fun addInvoice(invoice: InvoiceDomain): Long
    suspend fun updateInvoice(invoice: InvoiceDomain)
    suspend fun deleteInvoice(invoiceId: Long)
    fun searchInvoices(query: String): Flow<List<InvoiceDomain>>
    fun getDetectedAnomaliesCount(): Flow<Int>
    fun getInvoicesAboveThreshold(threshold: Double): Flow<List<InvoiceDomain>>
    fun getTotalInvoicedForCurrentMonth(): Flow<Double> // New method
    fun getMostFrequentClients(limit: Int): Flow<List<String>> // New method
    suspend fun addAnomaly(anomaly: AnomalyDomain)
}

class InvoiceRepository(private val invoiceDao: InvoiceDao) : IInvoiceRepository {

    override fun getInvoices(): Flow<List<InvoiceDomain>> {
        return invoiceDao.getAllInvoicesWithDetails().map {
            list -> list.map { it.toDomainModel() }
        }
    }

    override fun getInvoiceById(id: Long): Flow<InvoiceDomain?> {
        return invoiceDao.getInvoiceWithDetailsById(id).map {
            it?.toDomainModel()
        }
    }

    override suspend fun addInvoice(invoice: InvoiceDomain): Long {
        val clientEntity = invoice.client?.toEntity() // Handle nullable client
        val clientId = clientEntity?.let { invoiceDao.insertClient(it) } ?: invoice.client?.id ?: 0L

        val invoiceEntity = invoice.toEntity(clientId)
        val invoiceId = invoiceDao.insertInvoice(invoiceEntity)

        invoice.productLines.forEach {
            invoiceDao.insertProductLine(it.toEntity(invoiceId))
        }
        invoice.anomalies.forEach {
            invoiceDao.insertAnomaly(it.toEntity(invoiceId))
        }
        return invoiceId
    }

    override suspend fun updateInvoice(invoice: InvoiceDomain) {
        val clientEntity = invoice.client?.toEntity()
        val clientId = clientEntity?.let { invoiceDao.insertClient(it) } ?: invoice.client?.id ?: 0L

        invoiceDao.updateInvoice(invoice.toEntity(clientId))
        // Clear old product lines and anomalies, then insert new ones
        invoiceDao.deleteProductLinesForInvoice(invoice.id)
        invoice.productLines.forEach { invoiceDao.insertProductLine(it.toEntity(invoice.id)) }
        invoiceDao.deleteAnomaliesForInvoice(invoice.id)
        invoice.anomalies.forEach { invoiceDao.insertAnomaly(it.toEntity(invoice.id)) }
    }

    override suspend fun deleteInvoice(invoiceId: Long) {
        invoiceDao.deleteInvoice(invoiceId)
        invoiceDao.deleteProductLinesForInvoice(invoiceId)
        invoiceDao.deleteAnomaliesForInvoice(invoiceId)
        // Optionally delete client if no other invoices reference it
    }

    override fun searchInvoices(query: String): Flow<List<InvoiceDomain>> {
        return invoiceDao.searchInvoices("%${query}%").map {
            list -> list.map { it.toDomainModel() }
        }
    }

    override fun getDetectedAnomaliesCount(): Flow<Int> {
        return invoiceDao.getAnomaliesCount()
    }

    override fun getInvoicesAboveThreshold(threshold: Double): Flow<List<InvoiceDomain>> {
        return invoiceDao.getInvoicesAboveThreshold(threshold).map {
            list -> list.map { it.toDomainModel() }
        }
    }

    override fun getTotalInvoicedForCurrentMonth(): Flow<Double> {
        val calendar = Calendar.getInstance()
        val firstDayOfMonth = calendar.apply { set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis
        val lastDayOfMonth = calendar.apply { add(Calendar.MONTH, 1); set(Calendar.DAY_OF_MONTH, 1); add(Calendar.DATE, -1) }.timeInMillis
        return invoiceDao.getTotalAmountForPeriod(firstDayOfMonth, lastDayOfMonth)
    }

    override fun getMostFrequentClients(limit: Int): Flow<List<String>> {
        return invoiceDao.getMostFrequentClients(limit).map { clientNames ->
            clientNames // Assuming DAO returns List<String> directly
        }
    }

    override suspend fun addAnomaly(anomaly: AnomalyDomain) {
        invoiceDao.insertAnomaly(anomaly.toEntity(anomaly.invoiceId))
    }
}

// Mapper functions (assuming they are defined elsewhere or here for simplicity)
fun InvoiceWithDetails.toDomainModel(): InvoiceDomain {
    return InvoiceDomain(
        id = this.invoice.id,
        client = this.client?.toDomainModel(),
        invoiceNumber = this.invoice.invoiceNumber,
        date = this.invoice.date,
        totalHT = this.invoice.totalHT,
        totalTVA = this.invoice.totalTVA,
        totalTTC = this.invoice.totalTTC,
        productLines = this.productLines.map { it.toDomainModel() },
        anomalies = this.anomalies.map { it.toDomainModel() },
        ocrStatus = this.invoice.ocrStatus,
        isSuspect = this.invoice.isSuspect,
        isDuplicate = this.invoice.isDuplicate
    )
}

fun com.example.facturedz.data.model.Client.toDomainModel(): ClientDomain {
    return ClientDomain(id = this.id, name = this.name, rc = this.rc, nif = this.nif)
}

fun com.example.facturedz.data.model.ProductLine.toDomainModel(): ProductLineDomain {
    return ProductLineDomain(id = this.id, invoiceId = this.invoiceId, designation = this.designation, quantity = this.quantity, unitPrice = this.unitPrice, totalLinePrice = this.totalLinePrice)
}

fun com.example.facturedz.data.model.Anomaly.toDomainModel(): AnomalyDomain {
    return AnomalyDomain(id = this.id, invoiceId = this.invoiceId, type = this.type, description = this.description, justified = this.justified)
}

fun InvoiceDomain.toEntity(clientId: Long): com.example.facturedz.data.model.Invoice {
    return com.example.facturedz.data.model.Invoice(
        id = this.id,
        clientId = clientId,
        invoiceNumber = this.invoiceNumber,
        date = this.date,
        totalHT = this.totalHT,
        totalTVA = this.totalTVA,
        totalTTC = this.totalTTC,
        ocrStatus = this.ocrStatus,
        isSuspect = this.isSuspect,
        isDuplicate = this.isDuplicate
    )
}

fun ClientDomain.toEntity(): com.example.facturedz.data.model.Client {
    return com.example.facturedz.data.model.Client(id = this.id, name = this.name, rc = this.rc, nif = this.nif)
}

fun ProductLineDomain.toEntity(invoiceId: Long): com.example.facturedz.data.model.ProductLine {
    return com.example.facturedz.data.model.ProductLine(id = this.id, invoiceId = invoiceId, designation = this.designation, quantity = this.quantity, unitPrice = this.unitPrice, totalLinePrice = this.totalLinePrice)
}

fun AnomalyDomain.toEntity(invoiceId: Long): com.example.facturedz.data.model.Anomaly {
    return com.example.facturedz.data.model.Anomaly(id = this.id, invoiceId = invoiceId, type = this.type, description = this.description, justified = this.justified)
}

