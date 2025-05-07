package com.example.facturedz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.facturedz.data.model.ProductLine
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductLineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductLine(productLine: ProductLine): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProductLines(productLines: List<ProductLine>)

    @Update
    suspend fun updateProductLine(productLine: ProductLine)

    @Query("SELECT * FROM product_lines WHERE invoiceId = :invoiceId ORDER BY id ASC")
    fun getProductLinesForInvoice(invoiceId: Long): Flow<List<ProductLine>>

    @Query("DELETE FROM product_lines WHERE invoiceId = :invoiceId")
    suspend fun deleteProductLinesForInvoice(invoiceId: Long)

    @Query("DELETE FROM product_lines WHERE id = :productLineId")
    suspend fun deleteProductLine(productLineId: Long)
}
