package com.example.facturedz.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "product_lines",
    foreignKeys = [
        ForeignKey(
            entity = Invoice::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE // Si une facture est supprimée, ses lignes le sont aussi
        )
    ],
    indices = [Index(value = ["invoiceId"])]
)
data class ProductLine(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long,
    val designation: String,
    val quantity: Double, // Peut être Double pour des quantités non entières (ex: 1.5 kg)
    val unitPrice: Double,
    val totalLinePrice: Double
)
