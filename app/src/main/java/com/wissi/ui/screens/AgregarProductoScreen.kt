package com.wissi.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wissi.R
import com.wissi.data.model.Producto
import com.wissi.viewmodel.InventoryViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.rememberCoroutineScope
import com.wissi.utils.ImageUtils
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(
    navController: NavController,
    categoriaId: String,
    viewModel: InventoryViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedUri = uri
    }

    Column(modifier = Modifier.fillMaxSize()) {

        TopAppBar(
            title = { Text(stringResource(R.string.agregar_producto)) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text(stringResource(R.string.nombre)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text(stringResource(R.string.codigo)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text(stringResource(R.string.precio)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text(stringResource(R.string.cantidad)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN SELECCIONAR IMAGEN
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seleccionar Imagen")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PREVIEW
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(selectedUri ?: R.drawable.producto1)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen producto",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.producto1),
                error = painterResource(R.drawable.producto1)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN GUARDAR
            Button(
                onClick = {
                    scope.launch {
                        val savedImagePath = selectedUri?.let { uri ->
                            ImageUtils.saveImageToInternalStorage(
                                context, 
                                uri, 
                                ImageUtils.createUniqueImageFilename(UUID.randomUUID().toString())
                            )
                        }

                        val productoId = java.util.UUID.randomUUID().toString()
                        val producto = Producto(
                            id = productoId,
                            nombre = nombre,
                            codigo = codigo,
                            categoriaId = categoriaId,
                            precio = precio.toDoubleOrNull() ?: 0.0,
                            cantidad = cantidad.toIntOrNull() ?: 0,
                            imagenPath = savedImagePath,
                            imagenResId = R.drawable.producto1
                        )

                        viewModel.addProducto(producto)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nombre.isNotBlank() && codigo.isNotBlank()
            ) {
                Text(stringResource(R.string.guardar))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}