package com.wissi.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import android.util.Log
import com.wissi.data.model.Categoria
import com.wissi.data.model.Producto
import com.wissi.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import android.content.Context

data class InventoryUiState(
    val categorias: List<Categoria> = emptyList(),
    val productos: List<Producto> = emptyList(),
    val filteredCategorias: List<Categoria> = emptyList(),
    val filteredProductos: List<Producto> = emptyList(),
    val productosCategoria: List<Producto> = emptyList(),
    val filteredProductosCategoria: List<Producto> = emptyList(),
    val isAdmin: Boolean = false,
    val isLoggingIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategoriaId: String? = null,
    val selectedProducto: Producto? = null,
    val currentScreen: String = ""
)

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val repository = InventoryRepository(context)
    private val sharedPrefs = context.getSharedPreferences("wissi_prefs", Context.MODE_PRIVATE)
    private val TAG = "InventoryViewModel"
    
    private val _uiState = MutableStateFlow(
        InventoryUiState(
            isAdmin = sharedPrefs.getBoolean("is_admin_logged", false),
            isLoggingIn = false
        )
    )
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()
    
    private val _refreshEvent = MutableSharedFlow<Unit>(replay = 0)
    val refreshEvent: SharedFlow<Unit> = _refreshEvent.asSharedFlow()
    
    // ===== LOGIN =====
    fun loginAdmin(usuario: String, contrasena: String) {
        Log.d(TAG, "Intentando login admin")
        _uiState.update { it.copy(isLoggingIn = true, error = null) }
        
        if (usuario == "admin" && contrasena == "1234") {
            sharedPrefs.edit().putBoolean("is_admin_logged", true).apply()
            _uiState.update { it.copy(isAdmin = true, error = null, isLoggingIn = false) }
            Log.d(TAG, "Login exitoso")
        } else {
            _uiState.update { it.copy(error = "Credenciales inválidas", isLoggingIn = false) }
            Log.w(TAG, "Credenciales inválidas")
        }
    }
    
    fun logout() {
        Log.d(TAG, "Logout")
        sharedPrefs.edit().putBoolean("is_admin_logged", false).apply()
        _uiState.update { 
            it.copy(
                isAdmin = false,
                searchQuery = "",
                error = null,
                selectedProducto = null,
                selectedCategoriaId = null
            ) 
        }
    }
    
    // ===== CATEGORIAS =====
    fun loadCategorias() {
        Log.d(TAG, "loadCategorias()")
        if (_uiState.value.isLoading) {
            Log.d(TAG, "Ya está cargando, omitiendo llamada")
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                val lista = repository.getAllCategorias()
                _uiState.update { 
                    it.copy(
                        categorias = lista,
                        filteredCategorias = lista,
                        isLoading = false
                    ) 
                }
                Log.d(TAG, "Categorías cargadas: ${lista.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Error cargando categorías: ${e.message}", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error cargando categorías: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun searchCategorias(query: String) {
        Log.d(TAG, "searchCategorias($query)")
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(searchQuery = query, error = null) }
                val filtered = repository.searchCategorias(query)
                _uiState.update { it.copy(filteredCategorias = filtered) }
            } catch (e: Exception) {
                Log.e(TAG, "Error buscando categorías: ${e.message}", e)
                _uiState.update { 
                    it.copy(
                        error = "Error buscando: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun addCategoria(id: String, nombre: String) {
        Log.d(TAG, "addCategoria($id, $nombre)")
        
        if (id.isBlank() || nombre.isBlank()) {
            Log.w(TAG, "ID o nombre vacíos")
            _uiState.update { it.copy(error = "ID y nombre no pueden estar vacíos") }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                repository.addCategoria(Categoria(id, nombre))
                Log.d(TAG, "Categoría agregada, recargando lista")
                loadCategorias()
            } catch (e: Exception) {
                Log.e(TAG, "Error agregando categoría: ${e.message}", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun deleteCategoria(id: String) {
        Log.d(TAG, "deleteCategoria($id)")
        
        if (id.isBlank()) {
            Log.w(TAG, "ID de categoría vacío")
            _uiState.update { it.copy(error = "ID de categoría inválido") }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                repository.deleteCategoria(id)
                Log.d(TAG, "Categoría eliminada, recargando lista")
                loadCategorias()
            } catch (e: Exception) {
                Log.e(TAG, "Error eliminando categoría: ${e.message}", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    // ===== PRODUCTOS =====
    fun loadAllProductos() {
        Log.d(TAG, "loadAllProductos()")
        if (_uiState.value.isLoading) {
            Log.d(TAG, "Ya está cargando, omitiendo llamada")
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                val lista = repository.getAllProductos()
                _uiState.update { 
                    it.copy(
                        productos = lista,
                        filteredProductos = lista,
                        isLoading = false
                    ) 
                }
                Log.d(TAG, "Productos cargados: ${lista.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Error cargando productos: ${e.message}", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error cargando productos: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun searchProductos(query: String) {
        Log.d(TAG, "searchProductos($query)")
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(searchQuery = query, error = null) }
                val filtered = repository.searchProductos(query)
                _uiState.update { it.copy(filteredProductos = filtered) }
            } catch (e: Exception) {
                Log.e(TAG, "Error buscando productos: ${e.message}", e)
                _uiState.update { 
                    it.copy(
                        error = "Error buscando: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun loadProductosByCategoria(catId: String) {
        Log.d(TAG, "loadProductosByCategoria($catId)")
        
        if (catId.isBlank()) {
            Log.w(TAG, "ID de categoría vacío")
            _uiState.update { it.copy(error = "ID de categoría inválido") }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { 
                    it.copy(
                        selectedCategoriaId = catId,
                        searchQuery = "",
                        isLoading = true,
                        error = null
                    ) 
                }
                
                val lista = repository.getProductosByCategoria(catId)

                _uiState.update {
                    it.copy(
                        productosCategoria = lista,
                        filteredProductosCategoria = lista,
                        isLoading = false
                    )
                }
                Log.d(TAG, "Productos por categoría cargados: ${lista.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Error cargando productos por categoría: ${e.message}", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun searchProductosCategoria(query: String) {
        Log.d(TAG, "searchProductosCategoria($query)")
        viewModelScope.launch {
            try {
                val lista = _uiState.value.productosCategoria

                val filtered = if (query.isBlank()) {
                    lista
                } else {
                    lista.filter {
                        it.nombre.contains(query, true) ||
                                it.codigo.contains(query, true)
                    }
                }

                _uiState.update {
                    it.copy(
                        filteredProductosCategoria = filtered,
                        searchQuery = query,
                        error = null
                    )
                }
                Log.d(TAG, "Productos filtrados: ${filtered.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Error filtrando productos: ${e.message}", e)
                _uiState.update { 
                    it.copy(
                        error = "Error: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun addProducto(producto: Producto) {
        Log.d(TAG, "addProducto(${producto.nombre})")
        
        if (producto.nombre.isBlank() || producto.codigo.isBlank()) {
            Log.w(TAG, "Nombre o código del producto están vacíos")
            _uiState.update { it.copy(error = "Nombre y código son requeridos") }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                repository.addProducto(producto)
                Log.d(TAG, "Producto agregado, recargando listas")
                
                loadAllProductos()
                if (producto.categoriaId == _uiState.value.selectedCategoriaId) {
                    loadProductosByCategoria(producto.categoriaId)
                }
                _refreshEvent.emit(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Error agregando producto: ${e.message}", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun updateProducto(producto: Producto) {
        Log.d(TAG, "updateProducto(${producto.id})")
        
        if (producto.id.isBlank()) {
            Log.w(TAG, "ID del producto está vacío")
            _uiState.update { it.copy(error = "ID del producto inválido") }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                repository.updateProducto(producto)
                Log.d(TAG, "Producto actualizado, recargando listas")
                
                loadAllProductos()
                if (producto.categoriaId == _uiState.value.selectedCategoriaId) {
                    loadProductosByCategoria(producto.categoriaId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error actualizando producto: ${e.message}", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun deleteProducto(id: String) {
        Log.d(TAG, "deleteProducto($id)")
        
        if (id.isBlank()) {
            Log.w(TAG, "ID del producto está vacío")
            _uiState.update { it.copy(error = "ID del producto inválido") }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val categoriaId = _uiState.value.selectedProducto?.categoriaId
                
                repository.deleteProducto(id)
                Log.d(TAG, "Producto eliminado, recargando listas")
                
                loadAllProductos()
                if (categoriaId != null && categoriaId.isNotBlank()) {
                    loadProductosByCategoria(categoriaId)
                }
                _uiState.update { it.copy(selectedProducto = null) }
            } catch (e: Exception) {
                Log.e(TAG, "Error eliminando producto: ${e.message}", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun loadProductoById(id: String) {
        Log.d(TAG, "loadProductoById($id)")
        
        if (id.isBlank()) {
            Log.w(TAG, "ID del producto está vacío")
            _uiState.update { it.copy(selectedProducto = null, error = "ID inválido") }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                val producto = repository.getProductoById(id)
                _uiState.update { 
                    it.copy(
                        selectedProducto = producto,
                        isLoading = false
                    ) 
                }
                Log.d(TAG, "Producto cargado: ${producto != null}")
            } catch (e: Exception) {
                Log.e(TAG, "Error cargando producto: ${e.message}", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}",
                        selectedProducto = null
                    ) 
                }
            }
        }
    }
    
    fun selectProducto(producto: Producto) {
        Log.d(TAG, "selectProducto(${producto.id})")
        _uiState.update { it.copy(selectedProducto = producto) }
    }
    
    fun clearSelection() {
        Log.d(TAG, "clearSelection()")
        _uiState.update { it.copy(selectedProducto = null, selectedCategoriaId = null, searchQuery = "") }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun setCurrentScreen(screen: String) {
        Log.d(TAG, "setCurrentScreen($screen)")
        _uiState.update { it.copy(currentScreen = screen) }
    }
}
