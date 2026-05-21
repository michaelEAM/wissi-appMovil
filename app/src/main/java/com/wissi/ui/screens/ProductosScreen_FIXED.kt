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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wissi.R
import com.wissi.ui.components.ProductItem
import com.wissi.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    navController: NavController,
    viewModel: InventoryViewModel
) {

    // 🔥 CARGAR PRODUCTOS SOLO UNA VEZ AL ENTRAR A LA PANTALLA
    // NO usar Unit como key, esto causa recomposiciones infinitas
    LaunchedEffect(key1 = "ProductosScreen") {
        viewModel.clearError()
        viewModel.loadAllProductos()
    }

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            TopAppBar(
                title = {
                    Text(stringResource(R.string.productos_disponibles))
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            viewModel.clearSelection()
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },

                actions = {

                    if (uiState.isAdmin) {

                        TextButton(
                            onClick = {

                                val firstCatId =
                                    uiState.categorias.firstOrNull()?.id ?: "1"

                                navController.navigate(
                                    "agregar_producto/$firstCatId"
                                )
                            }
                        ) {

                            Text("Agregar")
                        }
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                // BUSCADOR
                OutlinedTextField(
                    value = uiState.searchQuery,

                    onValueChange = {
                        viewModel.searchProductos(it)
                    },

                    label = {
                        Text("Buscar producto")
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
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
                } else if (uiState.filteredProductos.isEmpty()) {
                    // SIN PRODUCTOS
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay productos disponibles")
                    }
                } else {
                    // LISTA DE PRODUCTOS
                    LazyColumn {
                        items(uiState.filteredProductos, key = { it.id }) { producto ->
                            ProductItem(
                                producto = producto,
                                viewModel = viewModel,
                                navController = navController,
                                showAdminActions = false,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // LOGO
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",

            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}
