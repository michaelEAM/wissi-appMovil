package com.wissi.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
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
        _uiState.update { it.copy(isLoggingIn = true, error = null) }
        if (usuario == "admin" && contrasena == "1234") {
            sharedPrefs.edit().putBoolean("is_admin_logged", true).apply()
            _uiState.update { it.copy(isAdmin = true, error = null, isLoggingIn = false) }
        } else {
            _uiState.update { it.copy(error = "Credenciales inválidas", isLoggingIn = false) }
        }
    }
    
    fun logout() {
        sharedPrefs.edit().putBoolean("is_admin_logged", false).apply()
        _uiState.update { 
            it.copy(
                isAdmin = false,
                searchQuery = "",
                error = null
            ) 
        }
    }
    
    // ===== CATEGORIAS =====
    fun loadCategorias() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val lista = repository.getAllCategorias()
            _uiState.update { 
                it.copy(
                    categorias = lista,
                    filteredCategorias = lista,
                    isLoading = false
                ) 
            }
        }
    }
    
    fun searchCategorias(query: String) {
        viewModelScope.launch {
            val filtered = repository.searchCategorias(query)
            _uiState.update { it.copy(filteredCategorias = filtered, searchQuery = query) }
        }
    }
    
    fun addCategoria(id: String, nombre: String) {
        if (id.isBlank() || nombre.isBlank()) return
        viewModelScope.launch {
            repository.addCategoria(Categoria(id, nombre))
            loadCategorias()
        }
    }
    
    fun deleteCategoria(id: String) {
        viewModelScope.launch {
            repository.deleteCategoria(id)
            loadCategorias()
            // También recargar productos si necesario
        }
    }
    
    // ===== PRODUCTOS =====
    fun loadAllProductos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val lista = repository.getAllProductos()
            _uiState.update { 
                it.copy(
                    productos = lista,
                    filteredProductos = lista,
                    isLoading = false
                ) 
            }
        }
    }
    
    fun searchProductos(query: String) {
        viewModelScope.launch {
            val filtered = repository.searchProductos(query)
            _uiState.update { it.copy(filteredProductos = filtered, searchQuery = query) }
        }
    }

    fun loadProductosByCategoria(catId: String) {
        viewModelScope.launch {

            val lista = repository.getProductosByCategoria(catId)

            _uiState.update {
                it.copy(
                    selectedCategoriaId = catId,
                    productosCategoria = lista,
                    filteredProductosCategoria = lista, // 🔥 CLAVE
                    searchQuery = "" // opcional pero recomendado
                )
            }
        }
    }

    fun searchProductosCategoria(query: String) {
        viewModelScope.launch {

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
                    searchQuery = query
                )
            }
        }
    }
    
    fun addProducto(producto: Producto) {
        viewModelScope.launch {
            repository.addProducto(producto)
            if (producto.categoriaId == _uiState.value.selectedCategoriaId) {
                loadProductosByCategoria(producto.categoriaId)
            }
            loadAllProductos()
            _refreshEvent.emit(Unit)
        }
    }
    
    fun updateProducto(producto: Producto) {
        viewModelScope.launch {
            repository.updateProducto(producto)
            loadAllProductos()
            if (producto.categoriaId == _uiState.value.selectedCategoriaId) {
                loadProductosByCategoria(producto.categoriaId)
            }
        }
    }
    
    fun deleteProducto(id: String) {
        viewModelScope.launch {
            _uiState.value.selectedProducto?.let { prod ->
                repository.deleteProducto(id)
                loadAllProductos()
                if (prod.categoriaId == _uiState.value.selectedCategoriaId) {
                    loadProductosByCategoria(prod.categoriaId)
                }
            }
        }
    }
    
    fun loadProductoById(id: String) {
        viewModelScope.launch {
            val producto = repository.getProductoById(id)
            _uiState.update { it.copy(selectedProducto = producto) }
        }
    }
    
    fun selectProducto(producto: Producto) {
        _uiState.update { it.copy(selectedProducto = producto) }
    }
    
    fun clearSelection() {
        _uiState.update { it.copy(selectedProducto = null, selectedCategoriaId = null) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun setCurrentScreen(screen: String) {
        _uiState.update { it.copy(currentScreen = screen) }
    }
}
