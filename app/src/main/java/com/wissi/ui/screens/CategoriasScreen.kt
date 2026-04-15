package com.wissi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    viewModel: InventoryViewModel // 👈 IMPORTANTE
) {

    LaunchedEffect(Unit) {
        viewModel.loadCategorias()
    }

    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        TopAppBar(
            title = { Text(stringResource(R.string.categorias)) },
            navigationIcon = {
                IconButton(onClick = { 
                    viewModel.logout()
                    navController.popBackStack() 
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            },
            actions = {
                if (uiState.isAdmin) {
                    Button(onClick = {
                        navController.navigate("agregar_categoria") {
                            launchSingleTop = true
                        }
                    }) {
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

            FilterField(
                query = uiState.searchQuery,
                onQueryChange = viewModel::searchCategorias,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(uiState.filteredCategorias) { categoria ->
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
