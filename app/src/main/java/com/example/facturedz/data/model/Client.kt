package com.example.facturedz.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val rc: String? = null, // Registre de Commerce
    val nif: String? = null, // Numéro d'Identification Fiscale
    val address: String? = null
)
