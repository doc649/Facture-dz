package com.example.facturedz.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.SET_NULL // Ou ForeignKey.CASCADE si suppression en cascade souhaitée
        )
    ],
    indices = [Index(value = ["clientId"])]
)
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientId: Long?,
    val invoiceNumber: String? = null,
    val date: Long, // Store as timestamp
    val totalHT: Double,
    val totalTVA: Double,
    val totalTTC: Double,
    val filePath: String? = null, // Path to the original PDF/image file
    var ocrStatus: String = "PENDING", // PENDING, SUCCESS, FAILED
    val notes: String? = null,
    var isSuspect: Boolean = false,
    var anomalyJustified: Boolean = false
)
