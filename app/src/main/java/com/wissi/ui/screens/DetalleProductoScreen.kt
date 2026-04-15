package com.wissi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.wissi.R
import com.wissi.utils.ImageUtils
import com.wissi.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    navController: NavController,
    productoId: String,
    viewModel: InventoryViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val producto = uiState.selectedProducto

    LaunchedEffect(productoId) {
        if (productoId.isNotEmpty()) {
            viewModel.loadProductoById(productoId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(producto?.nombre ?: "Detalle producto")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.loadProductoById(productoId)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recargar"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            producto?.let {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(
                                if (it.imagenPath != null) {
                                    ImageUtils.getImageUri(
                                        LocalContext.current,
                                        it.imagenPath
                                    )
                                } else it.imagenResId
                            )
                            .crossfade(true)
                            .build(),
                        contentDescription = it.nombre,
                        modifier = Modifier
                            .size(400.dp)
                            .padding(bottom = 24.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.producto1),
                        error = painterResource(R.drawable.producto1)
                    )

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            InfoRow("Código", it.codigo)
                            InfoRow(
                                "Categoría",
                                uiState.categorias
                                    .find { cat -> cat.id == it.categoriaId }
                                    ?.nombre ?: it.categoriaId
                            )
                            InfoRow("Precio", "$${it.precio.toInt()}")
                            InfoRow("Cantidad", "${it.cantidad} unidades")
                        }
                    }
                }

            } ?: if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Producto no encontrado")
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
