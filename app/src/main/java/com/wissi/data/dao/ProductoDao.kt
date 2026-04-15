package com.wissi.data.dao

import androidx.room.*
import com.wissi.data.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos")
    fun getAllProductos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos")
    suspend fun getAllProductosSync(): List<Producto>

    @Query("SELECT * FROM productos WHERE categoria_id = :categoriaId")
    fun getProductosByCategoria(categoriaId: String): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE categoria_id = :categoriaId")
    suspend fun getProductosByCategoriaSync(categoriaId: String): List<Producto>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: String): Producto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: Producto)

    @Update
    suspend fun updateProducto(producto: Producto): Int

    @Delete
    suspend fun deleteProducto(producto: Producto)

    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :query || '%' OR codigo LIKE '%' || :query || '%' OR categoria_id IN (SELECT id FROM categorias WHERE nombre LIKE '%' || :query || '%')")
    fun searchProductos(query: String): Flow<List<Producto>>
}
