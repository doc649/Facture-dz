package com.example.facturedz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.facturedz.data.model.Invoice
import com.example.facturedz.data.model.InvoiceWithDetails // Assuming this will be created for relations
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice): Long

    @Update
    suspend fun updateInvoice(invoice: Invoice)

    @Query("DELETE FROM invoices WHERE id = :invoiceId")
    suspend fun deleteInvoice(invoiceId: Long)

    @Transaction
    @Query("SELECT * FROM invoices ORDER BY date DESC")
    fun getAllInvoicesWithDetails(): Flow<List<InvoiceWithDetails>> // For list view with client info

    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    fun getInvoiceWithDetailsById(invoiceId: Long): Flow<InvoiceWithDetails?>

    // Basic query for invoice object if needed without all details
    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    fun getInvoiceById(invoiceId: Long): Flow<Invoice?>

    // Search queries - can be expanded
    @Query("SELECT * FROM invoices WHERE invoiceNumber LIKE :query OR notes LIKE :query ORDER BY date DESC")
    fun searchInvoices(query: String): Flow<List<InvoiceWithDetails>>

    // Query to detect duplicates (same client, date, amount)
    @Query("SELECT * FROM invoices WHERE clientId = :clientId AND date = :date AND totalTTC = :totalTTC AND id != :currentInvoiceId")
    suspend fun findDuplicateInvoices(clientId: Long?, date: Long, totalTTC: Double, currentInvoiceId: Long): List<Invoice>

    @Query("SELECT * FROM invoices WHERE totalTTC > :threshold AND isSuspect = 0 AND anomalyJustified = 0 ORDER BY date DESC")
    fun getInvoicesAboveThreshold(threshold: Double): Flow<List<InvoiceWithDetails>>
}
