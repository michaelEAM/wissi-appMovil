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
import com.wissi.ui.components.ProductItem
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wissi.R
import com.wissi.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    navController: NavController,
    viewModel: InventoryViewModel
) {

    LaunchedEffect(Unit) {
        viewModel.clearError()
        viewModel.searchProductos("") // Limpia filtro, muestra todos
        viewModel.loadAllProductos()
    }
    
    LaunchedEffect(viewModel.refreshEvent) {
        viewModel.refreshEvent.collect {
            viewModel.loadAllProductos()
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            TopAppBar(
                title = { Text(stringResource(R.string.productos_disponibles)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                                val firstCatId = uiState.categorias.firstOrNull()?.id ?: "1"
                                navController.navigate("agregar_producto/$firstCatId")
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

                // 🔍 CAMPO DE BÚSQUEDA (reemplaza FilterField)
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = {
                        viewModel.searchProductos(it)
                    },
                    label = { Text("Buscar producto") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // 📦 LISTA DE PRODUCTOS
                LazyColumn {
                    items(uiState.filteredProductos) { producto ->
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