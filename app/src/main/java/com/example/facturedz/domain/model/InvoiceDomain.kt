package com.example.facturedz.domain.model

// Domain model for Invoice
data class InvoiceDomain(
    val id: Long = 0,
    val client: ClientDomain?,
    val invoiceNumber: String? = null,
    val date: Long, // Timestamp
    val totalHT: Double,
    val totalTVA: Double,
    val totalTTC: Double,
    val filePath: String? = null,
    var ocrStatus: String = "PENDING",
    val notes: String? = null,
    var isSuspect: Boolean = false,
    var anomalyJustified: Boolean = false,
    val productLines: List<ProductLineDomain> = emptyList(),
    val anomalies: List<AnomalyDomain> = emptyList()
)

// Mappers would be more complex here, especially if InvoiceWithDetails is the primary entity from data layer
// For simplicity, assuming direct mapping or a more complex mapper in a dedicated file.

// Domain model for ProductLine
data class ProductLineDomain(
    val id: Long = 0,
    val invoiceId: Long,
    val designation: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalLinePrice: Double
)

// Domain model for Anomaly
data class AnomalyDomain(
    val id: Long = 0,
    val invoiceId: Long,
    val type: String,
    val description: String,
    var status: String = "DETECTED"
)

// Example mappers (simplified - in a real app, these might be in a separate mapper class or file)
fun com.example.facturedz.data.model.ProductLine.toDomain() = ProductLineDomain(
    id = id,
    invoiceId = invoiceId,
    designation = designation,
    quantity = quantity,
    unitPrice = unitPrice,
    totalLinePrice = totalLinePrice
)

fun ProductLineDomain.toEntity() = com.example.facturedz.data.model.ProductLine(
    id = id,
    invoiceId = invoiceId,
    designation = designation,
    quantity = quantity,
    unitPrice = unitPrice,
    totalLinePrice = totalLinePrice
)

fun com.example.facturedz.data.model.Anomaly.toDomain() = AnomalyDomain(
    id = id,
    invoiceId = invoiceId,
    type = type,
    description = description,
    status = status
)

fun AnomalyDomain.toEntity() = com.example.facturedz.data.model.Anomaly(
    id = id,
    invoiceId = invoiceId,
    type = type,
    description = description,
    status = status
)

// Mapper for InvoiceWithDetails to InvoiceDomain
fun com.example.facturedz.data.model.InvoiceWithDetails.toDomain(): InvoiceDomain {
    return InvoiceDomain(
        id = this.invoice.id,
        client = this.client?.toDomain(),
        invoiceNumber = this.invoice.invoiceNumber,
        date = this.invoice.date,
        totalHT = this.invoice.totalHT,
        totalTVA = this.invoice.totalTVA,
        totalTTC = this.invoice.totalTTC,
        filePath = this.invoice.filePath,
        ocrStatus = this.invoice.ocrStatus,
        notes = this.invoice.notes,
        isSuspect = this.invoice.isSuspect,
        anomalyJustified = this.invoice.anomalyJustified,
        productLines = this.productLines.map { it.toDomain() },
        anomalies = this.anomalies.map { it.toDomain() }
    )
}

// Mapper from InvoiceDomain to Invoice entity (without relations, as those are handled separately)
fun InvoiceDomain.toInvoiceEntity() = com.example.facturedz.data.model.Invoice(
    id = id,
    clientId = client?.id,
    invoiceNumber = invoiceNumber,
    date = date,
    totalHT = totalHT,
    totalTVA = totalTVA,
    totalTTC = totalTTC,
    filePath = filePath,
    ocrStatus = ocrStatus,
    notes = notes,
    isSuspect = isSuspect,
    anomalyJustified = anomalyJustified
)

