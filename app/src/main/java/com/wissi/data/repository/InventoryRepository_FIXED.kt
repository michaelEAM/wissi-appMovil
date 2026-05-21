package com.wissi.data.repository

import android.content.Context
import android.util.Log
import com.wissi.R
import com.wissi.data.model.Categoria
import com.wissi.data.model.Producto
import com.wissi.data.supabase.Category
import com.wissi.data.supabase.Product
import com.wissi.data.supabase.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import java.util.UUID

class InventoryRepository(private val context: Context) {

    private val supabase = SupabaseClientProvider.client
    private val TAG = "InventoryRepository"

    // ===== CONVERSIONES =====

    private fun Product.toDomain(): Producto {

        return Producto(
            id = id?.toString() ?: UUID.randomUUID().toString(),
            nombre = name,
            codigo = code,

            // 🔥 FIX CLAVE
            categoriaId = categoryId?.toString() ?: "",

            precio = price,
            cantidad = stock,
            imagenPath = imageUrl,
            imagenResId = R.drawable.producto1
        )
    }


    private fun Categoria.toSupabase(): Category {

        return Category(
            id = id.toIntOrNull(),
            name = nombre.ifBlank { "Sin nombre" }
        )
    }

    private fun Producto.toSupabase(): Product {

        return Product(
            id = id.toIntOrNull(),
            name = nombre,
            code = codigo,
            description = null,
            price = precio,
            stock = cantidad,

            // 🔥 CLAVE
            categoryId = categoriaId.toIntOrNull(),

            imageUrl = imagenPath
        )
    }

    private fun Category.toDomain(): Categoria {

        return try {

            Categoria(
                id = id?.toString() ?: UUID.randomUUID().toString(),
                nombre = name.ifBlank { "Sin nombre" }
            )

        } catch (e: Exception) {

            Log.e(TAG, "Error convirtiendo Category a Categoria: ${e.message}")

            Categoria(
                id = UUID.randomUUID().toString(),
                nombre = "Error en conversión"
            )
        }
    }

    // ===== CATEGORIAS =====

    suspend fun getAllCategorias(): List<Categoria> {

        return try {

            Log.d(TAG, "Obteniendo todas las categorías...")

            val categorias = supabase
                .from("categories")
                .select()
                .decodeList<Category>()

            Log.d(TAG, "Categorías obtenidas: ${categorias.size}")

            categorias.map { it.toDomain() }

        } catch (e: Exception) {

            Log.e(TAG, "Error obteniendo categorías: ${e.message}", e)

            emptyList()
        }
    }

    suspend fun addCategoria(categoria: Categoria) {

        try {

            Log.d(TAG, "Agregando categoría: ${categoria.nombre}")

            val validateCat = categoria.toSupabase()

            supabase
                .from("categories")
                .insert(validateCat)

            Log.d(TAG, "Categoría agregada exitosamente")

        } catch (e: Exception) {

            Log.e(TAG, "Error agregando categoría: ${e.message}", e)

            throw IllegalStateException(
                "No se pudo agregar la categoría: ${e.message}"
            )
        }
    }

    suspend fun deleteCategoria(id: String) {

        try {

            if (id.isBlank()) {
                throw IllegalArgumentException("ID de categoría vacío")
            }

            Log.d(TAG, "Eliminando categoría: $id")

            supabase
                .from("categories")
                .delete {
                    filter {
                        eq("id", id.toIntOrNull() ?: 0)
                    }
                }

            Log.d(TAG, "Categoría eliminada exitosamente")

        } catch (e: Exception) {

            Log.e(TAG, "Error eliminando categoría: ${e.message}", e)

            throw IllegalStateException(
                "No se pudo eliminar la categoría: ${e.message}"
            )
        }
    }

    // ===== BUSCAR CATEGORIAS =====

    suspend fun searchCategorias(query: String): List<Categoria> {

        return try {

            Log.d(TAG, "Buscando categorías con query: $query")

            if (query.isBlank()) {

                getAllCategorias()

            } else {

                val categorias = supabase
                    .from("categories")
                    .select()
                    .decodeList<Category>()

                val filtered = categorias.filter {
                    it.name.contains(query, ignoreCase = true)
                }.map { it.toDomain() }

                Log.d(TAG, "Categorías encontradas: ${filtered.size}")

                filtered
            }

        } catch (e: Exception) {

            Log.e(TAG, "Error buscando categorías: ${e.message}", e)

            emptyList()
        }
    }

    // ===== PRODUCTOS =====

    suspend fun getAllProductos(): List<Producto> {

        return try {

            Log.d(TAG, "Obteniendo todos los productos...")

            val productos = supabase
                .from("products")
                .select()
                .decodeList<Product>()

            Log.d(TAG, "Productos obtenidos: ${productos.size}")

            productos.map { it.toDomain() }

        } catch (e: Exception) {

            Log.e(TAG, "Error obteniendo productos: ${e.message}", e)

            emptyList()
        }
    }

    suspend fun addProducto(producto: Producto) {

        try {

            Log.d(TAG, "Agregando producto: ${producto.nombre}")

            if (
                producto.nombre.isBlank() ||
                producto.codigo.isBlank()
            ) {
                throw IllegalArgumentException(
                    "Nombre o código del producto están vacíos"
                )
            }

            val validateProd = producto.toSupabase()

            supabase
                .from("products")
                .insert(validateProd)

            Log.d(TAG, "Producto agregado exitosamente")

        } catch (e: Exception) {

            Log.e(TAG, "Error agregando producto: ${e.message}", e)

            throw IllegalStateException(
                "No se pudo agregar el producto: ${e.message}"
            )
        }
    }

    suspend fun deleteProducto(id: String) {

        try {

            if (id.isBlank()) {
                throw IllegalArgumentException("ID de producto vacío")
            }

            Log.d(TAG, "Eliminando producto: $id")

            supabase
                .from("products")
                .delete {
                    filter {
                        eq("id", id.toIntOrNull() ?: 0)
                    }
                }

            Log.d(TAG, "Producto eliminado exitosamente")

        } catch (e: Exception) {

            Log.e(TAG, "Error eliminando producto: ${e.message}", e)

            throw IllegalStateException(
                "No se pudo eliminar el producto: ${e.message}"
            )
        }
    }

    suspend fun updateProducto(producto: Producto) {

        try {

            if (producto.id.isBlank()) {
                throw IllegalArgumentException("ID de producto vacío")
            }

            Log.d(TAG, "Actualizando producto: ${producto.id}")

            val validateProd = producto.toSupabase()

            supabase
                .from("products")
                .update(validateProd) {
                    filter {
                        eq("id", producto.id.toIntOrNull() ?: 0)
                    }
                }

            Log.d(TAG, "Producto actualizado exitosamente")

        } catch (e: Exception) {

            Log.e(TAG, "Error actualizando producto: ${e.message}", e)

            throw IllegalStateException(
                "No se pudo actualizar el producto: ${e.message}"
            )
        }
    }

    // ===== BUSCAR PRODUCTOS =====

    suspend fun searchProductos(query: String): List<Producto> {

        return try {

            Log.d(TAG, "Buscando productos con query: $query")

            if (query.isBlank()) {

                getAllProductos()

            } else {

                val productos = supabase
                    .from("products")
                    .select()
                    .decodeList<Product>()

                val filtered = productos.filter {

                    it.name.contains(query, ignoreCase = true) ||
                            it.code.contains(query, ignoreCase = true)

                }.map { it.toDomain() }

                Log.d(TAG, "Productos encontrados: ${filtered.size}")

                filtered
            }

        } catch (e: Exception) {

            Log.e(TAG, "Error buscando productos: ${e.message}", e)

            emptyList()
        }
    }

    // ===== PRODUCTO POR ID =====

    suspend fun getProductoById(id: String): Producto? {

        return try {

            if (id.isBlank()) {

                Log.w(TAG, "ID de producto vacío")

                null

            } else {

                Log.d(TAG, "Obteniendo producto: $id")

                val producto = supabase
                    .from("products")
                    .select()
                    .decodeList<Product>()
                    .find {
                        it.id?.toString() == id
                    }

                val result = producto?.toDomain()

                Log.d(TAG, "Producto encontrado: ${result != null}")

                result
            }

        } catch (e: Exception) {

            Log.e(TAG, "Error obteniendo producto por ID: ${e.message}", e)

            null
        }
    }

    // ===== PRODUCTOS POR CATEGORIA =====

    suspend fun getProductosByCategoria(
        categoriaId: String
    ): List<Producto> {

        return try {

            if (categoriaId.isBlank()) {

                Log.w(TAG, "ID de categoría vacío")

                emptyList()

            } else {

                Log.d(
                    TAG,
                    "Obteniendo productos de categoría: $categoriaId"
                )

                val productos = supabase
                    .from("products")
                    .select()
                    .decodeList<Product>()

                val filtered = productos.filter {

                    it.categoryId?.toString() == categoriaId

                }.map { it.toDomain() }

                Log.d(
                    TAG,
                    "Productos de categoría encontrados: ${filtered.size}"
                )

                filtered
            }

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Error obteniendo productos por categoría: ${e.message}",
                e
            )

            emptyList()
        }
    }
}
