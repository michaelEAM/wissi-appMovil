package com.wissi.data.dao

import androidx.room.*
import com.wissi.data.model.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias")
    fun getAllCategorias(): Flow<List<Categoria>>

    @Query("SELECT * FROM categorias")
    suspend fun getAllCategoriasSync(): List<Categoria>

    @Query("SELECT COUNT(*) FROM categorias")
    suspend fun getAllCategoriasCount(): Long

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun getCategoriaById(id: String): Categoria?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoria(categoria: Categoria)

    @Delete
    suspend fun deleteCategoria(categoria: Categoria)

    @Query("SELECT * FROM categorias WHERE nombre LIKE '%' || :query || '%'")
    fun searchCategorias(query: String): Flow<List<Categoria>>
}
