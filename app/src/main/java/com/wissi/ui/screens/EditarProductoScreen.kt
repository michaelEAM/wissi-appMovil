package com.wissi.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.wissi.R
import com.wissi.utils.ImageUtils
import com.wissi.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(
    navController: NavController,
    productoId: String,
    viewModel: InventoryViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val producto = uiState.selectedProducto

    var nombre by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var imagePath by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedUri = uri
        imagePath = null
    }

    // 🔥 CARGAR DATOS CUANDO EL PRODUCTO EXISTE
    LaunchedEffect(producto) {
        producto?.let {
            nombre = it.nombre
            codigo = it.codigo
            precio = it.precio.toString()
            cantidad = it.cantidad.toString()
            imagePath = it.imagenPath
        }
    }

    if (producto == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Producto no encontrado")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // 🔥 AQUÍ
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TopAppBar(
            title = { Text("Editar ${producto.nombre}") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("📸 Cambiar Imagen")
        }

        Spacer(modifier = Modifier.height(16.dp))

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(
                    selectedUri ?: imagePath?.let {
                        ImageUtils.getImageUri(context, it)
                    } ?: producto.imagenResId
                )
                .crossfade(true)
                .build(),
            contentDescription = "Imagen producto",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.producto1),
            error = painterResource(R.drawable.producto1)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = codigo,
            onValueChange = { codigo = it },
            label = { Text("Código") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it },
            label = { Text("Cantidad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    val pPrecio = precio.toDoubleOrNull() ?: 0.0
                    val pCantidad = cantidad.toIntOrNull() ?: 0

                    val savedImagePath = selectedUri?.let { uri ->
                        ImageUtils.saveImageToInternalStorage(
                            context,
                            uri,
                            ImageUtils.createUniqueImageFilename(producto.id)
                        )
                    }

                    val updated = producto.copy(
                        nombre = nombre,
                        codigo = codigo,
                        precio = pPrecio,
                        cantidad = pCantidad,
                        imagenPath = savedImagePath ?: imagePath
                    )

                    viewModel.updateProducto(updated)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Actualizar")
        }
    }
}