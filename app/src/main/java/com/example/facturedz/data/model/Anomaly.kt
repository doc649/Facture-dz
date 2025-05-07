package com.example.facturedz.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "anomalies",
    foreignKeys = [
        ForeignKey(
            entity = Invoice::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE // Si une facture est supprimée, ses anomalies le sont aussi
        )
    ],
    indices = [Index(value = ["invoiceId"])]
)
data class Anomaly(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long,
    val type: String, // e.g., "TVA_MISMATCH", "TOTAL_HT_MISMATCH", "DUPLICATE_INVOICE", "SUSPICIOUS_AMOUNT"
    val description: String,
    var status: String = "DETECTED" // e.g., "DETECTED", "JUSTIFIED"
)
