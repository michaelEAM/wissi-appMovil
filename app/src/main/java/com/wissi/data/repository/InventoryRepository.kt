package com.wissi.data.repository

import android.content.Context
import com.wissi.R
import com.wissi.data.database.AppDatabase
import com.wissi.data.model.Categoria
import com.wissi.data.model.Producto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class InventoryRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val categoriaDao = db.categoriaDao()
    private val productoDao = db.productoDao()



    // ===== CATEGORIAS =====
    suspend fun getAllCategorias(): List<Categoria> {
        return categoriaDao.getAllCategoriasSync()
    }

    suspend fun getCategoriaById(id: String): Categoria? {
        return categoriaDao.getCategoriaById(id)
    }

    suspend fun addCategoria(categoria: Categoria) {
        categoriaDao.insertCategoria(categoria)
    }

    suspend fun deleteCategoria(id: String): Boolean {
        getCategoriaById(id)?.let { categoria ->
            categoriaDao.deleteCategoria(categoria)
            return true
        }
        return false
    }

    suspend fun searchCategorias(query: String): List<Categoria> {
        return if (query.isEmpty()) {
            getAllCategorias()
        } else {
            categoriaDao.searchCategorias(query).first() // Convert Flow to List
        }
    }

    // ===== PRODUCTOS =====
    suspend fun getAllProductos(): List<Producto> {
        return productoDao.getAllProductosSync()
    }

    suspend fun getProductoById(id: String): Producto? {
        return productoDao.getProductoById(id)
    }

    suspend fun getProductosByCategoria(categoriaId: String): List<Producto> {
        return productoDao.getProductosByCategoriaSync(categoriaId)
    }

    suspend fun addProducto(producto: Producto) {
        productoDao.insertProducto(producto)
    }

    suspend fun deleteProducto(id: String): Boolean {
        getProductoById(id)?.let { producto ->
            productoDao.deleteProducto(producto)
            return true
        }
        return false
    }

    suspend fun updateProducto(producto: Producto): Boolean {
        val result = productoDao.updateProducto(producto)
        return result > 0
    }

    suspend fun searchProductos(query: String): List<Producto> {
        return if (query.isEmpty()) {
            getAllProductos()
        } else {
            productoDao.searchProductos(query).first()
        }
    }
}
