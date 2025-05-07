package com.example.facturedz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.facturedz.data.model.Anomaly
import kotlinx.coroutines.flow.Flow

@Dao
interface AnomalyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnomaly(anomaly: Anomaly): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAnomalies(anomalies: List<Anomaly>)

    @Update
    suspend fun updateAnomaly(anomaly: Anomaly)

    @Query("SELECT * FROM anomalies WHERE invoiceId = :invoiceId ORDER BY id ASC")
    fun getAnomaliesForInvoice(invoiceId: Long): Flow<List<Anomaly>>

    @Query("DELETE FROM anomalies WHERE invoiceId = :invoiceId")
    suspend fun deleteAnomaliesForInvoice(invoiceId: Long)

    @Query("DELETE FROM anomalies WHERE id = :anomalyId")
    suspend fun deleteAnomaly(anomalyId: Long)

    @Query("SELECT COUNT(*) FROM anomalies WHERE status = 'DETECTED'")
    fun getDetectedAnomaliesCount(): Flow<Int>
}
