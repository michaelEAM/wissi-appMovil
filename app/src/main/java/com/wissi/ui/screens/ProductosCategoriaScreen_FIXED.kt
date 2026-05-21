package com.wissi.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wissi.R
import com.wissi.ui.components.FilterField
import com.wissi.ui.components.ProductItem
import com.wissi.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosCategoriaScreen(
    navController: NavController,
    categoriaId: String,
    viewModel: InventoryViewModel
) {

    // 🔥 CARGAR PRODUCTOS CUANDO CAMBIA CATEGORIA ID
    LaunchedEffect(key1 = categoriaId) {
        if (categoriaId.isNotBlank()) {
            viewModel.loadProductosByCategoria(categoriaId)
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        // TOP BAR
        TopAppBar(
            title = { 
                Text("Productos de la categoría") 
            },
            navigationIcon = {
                IconButton(onClick = { 
                    viewModel.clearSelection()
                    navController.popBackStack() 
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                // BOTÓN AGREGAR (SOLO PARA ADMIN)
                if (uiState.isAdmin && categoriaId.isNotBlank()) {
                    TextButton(
                        onClick = {
                            navController.navigate("agregar_producto/$categoriaId")
                        }
                    ) {
                        Text("Agregar")
                    }
                }
            }
        )

        // CONTENIDO
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                // BUSCADOR
                FilterField(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::searchProductosCategoria,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // MENSAJE DE ERROR
                if (!uiState.error.isNullOrBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                // INDICADOR DE CARGA
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.filteredProductosCategoria.isEmpty()) {
                    // SIN PRODUCTOS
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay productos en esta categoría")
                    }
                } else {
                    // LISTA DE PRODUCTOS
                    LazyColumn {
                        items(uiState.filteredProductosCategoria, key = { it.id }) { producto ->
                            ProductItem(
                                producto = producto,
                                viewModel = viewModel,
                                navController = navController,
                                showAdminActions = uiState.isAdmin
                            )
                        }
                    }
                }
            }

            // LOGO
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }
    }
}
