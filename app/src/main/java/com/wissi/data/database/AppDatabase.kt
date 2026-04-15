package com.wissi.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.wissi.data.dao.CategoriaDao
import com.wissi.data.dao.ProductoDao
import com.wissi.data.model.Categoria
import com.wissi.data.model.Producto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [Categoria::class, Producto::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriaDao(): CategoriaDao
    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wissi_inventory_database"
                ).build()
                INSTANCE = instance
                // Populate on first create
                instance
            }
        }
    }
}
