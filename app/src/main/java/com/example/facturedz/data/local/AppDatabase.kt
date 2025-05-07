package com.example.facturedz.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.facturedz.data.model.Anomaly
import com.example.facturedz.data.model.Client
import com.example.facturedz.data.model.Invoice
import com.example.facturedz.data.model.ProductLine

@Database(
    entities = [Invoice::class, Client::class, ProductLine::class, Anomaly::class],
    version = 1,
    exportSchema = false // Schema exportation is good for complex projects, but can be false for now
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun invoiceDao(): InvoiceDao
    abstract fun clientDao(): ClientDao
    abstract fun productLineDao(): ProductLineDao
    abstract fun anomalyDao(): AnomalyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "facture_dz_database"
                )
                // .fallbackToDestructiveMigration() // Consider migration strategy for production
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
