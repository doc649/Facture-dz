package com.example.facturedz.data.model

import androidx.room.Embedded
import androidx.room.Relation

// Data class to hold an Invoice with its related Client and list of ProductLines and Anomalies
data class InvoiceWithDetails(
    @Embedded val invoice: Invoice,

    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client: Client?,

    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId",
        entity = ProductLine::class
    )
    val productLines: List<ProductLine>,

    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId",
        entity = Anomaly::class
    )
    val anomalies: List<Anomaly>
)
