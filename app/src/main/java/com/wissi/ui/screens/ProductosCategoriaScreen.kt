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

    LaunchedEffect(categoriaId) {
        viewModel.loadProductosByCategoria(categoriaId)
    }


    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        // 🔥 TOP BAR
        TopAppBar(
            title = { Text("Productos - $categoriaId") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                // 🔥 BOTÓN AGREGAR (VISIBLE COMO EN EL OTRO SCREEN)
                if (uiState.isAdmin) {
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

        // 🔵 CONTENIDO
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                FilterField(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::searchProductosCategoria,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn {
                    items(uiState.filteredProductosCategoria) { producto ->
                        ProductItem(
                            producto = producto,
                            viewModel = viewModel,
                            navController = navController,
                            showAdminActions = uiState.isAdmin
                        )
                    }
                }
            }

            // 🔥 LOGO (MISMO ESTILO QUE EL QUE FUNCIONA)
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
