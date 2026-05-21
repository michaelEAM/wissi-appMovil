package com.wissi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wissi.R
import com.wissi.ui.components.CategoriaItem
import com.wissi.ui.components.FilterField
import com.wissi.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    navController: NavController,
    viewModel: InventoryViewModel
) {

    // 🔥 CARGAR CATEGORIAS SOLO UNA VEZ AL ENTRAR A LA PANTALLA
    // Usar una key específica para evitar recomposiciones infinitas
    LaunchedEffect(key1 = "CategoriasScreen") {
        viewModel.clearError()
        viewModel.loadCategorias()
    }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopAppBar(

            title = {
                Text(stringResource(R.string.categorias))
            },

            navigationIcon = {

                IconButton(
                    onClick = {
                        viewModel.clearSelection()
                        navController.popBackStack()
                    }
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            },

            actions = {

                if (uiState.isAdmin) {

                    Button(
                        onClick = {

                            navController.navigate("agregar_categoria") {
                                launchSingleTop = true
                            }
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
            FilterField(
                query = uiState.searchQuery,

                onQueryChange = {
                    viewModel.searchCategorias(it)
                },

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
            } else if (uiState.filteredCategorias.isEmpty()) {
                // SIN CATEGORÍAS
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay categorías disponibles")
                }
            } else {
                // LISTA DE CATEGORIAS
                LazyColumn {
                    items(uiState.filteredCategorias, key = { it.id }) { categoria ->
                        CategoriaItem(
                            categoria = categoria,
                            viewModel = viewModel,
                            navController = navController,
                            isAdmin = uiState.isAdmin
                        )
                    }
                }
            }
        }
    }
}
