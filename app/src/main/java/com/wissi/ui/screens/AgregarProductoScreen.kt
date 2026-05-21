package com.wissi.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
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
import kotlinx.coroutines.launch
import java.util.UUID
import com.wissi.data.supabase.SupabaseStorageManager
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import java.io.File

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
    var isLoading by remember { mutableStateOf(false) }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Crear archivo temporal para cámara
    fun createImageFile(): File {

        val storageDir = context.cacheDir

        return File(
            storageDir,
            "camera_${System.currentTimeMillis()}.jpg"
        )
    }

    // Launcher galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        selectedUri = uri
    }

    // Launcher cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->

        if (success) {

            currentPhotoUri?.let {

                selectedUri = null
                selectedUri = it
            }
        }
    }
    // Launcher permiso cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->

        if (granted) {

            val photoFile = createImageFile()

            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )

            currentPhotoUri = photoUri
            selectedUri = photoUri

            cameraLauncher.launch(photoUri)

        } else {

            println("Permiso de cámara denegado")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopAppBar(
            title = {
                Text(stringResource(R.string.agregar_producto))
            },
            navigationIcon = {

                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {

                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
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
                onValueChange = {
                    nombre = it
                },
                label = {
                    Text(stringResource(R.string.nombre))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = codigo,
                onValueChange = {
                    codigo = it
                },
                label = {
                    Text(stringResource(R.string.codigo))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = {
                    precio = it
                },
                label = {
                    Text(stringResource(R.string.precio))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cantidad,
                onValueChange = {
                    cantidad = it
                },
                label = {
                    Text(stringResource(R.string.cantidad))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // BOTONES IMAGEN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // GALERÍA
                Button(
                    onClick = {

                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier.weight(1f)
                ) {

                    Text("Galería")
                }

                // CÁMARA
                Button(
                    onClick = {

                        when {

                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED -> {

                                val photoFile = createImageFile()

                                val photoUri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    photoFile
                                )

                                currentPhotoUri = photoUri
                                selectedUri = photoUri

                                cameraLauncher.launch(photoUri)
                            }

                            else -> {

                                cameraPermissionLauncher.launch(
                                    Manifest.permission.CAMERA
                                )
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {

                    Text("Tomar Foto")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PREVIEW IMAGEN
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(selectedUri ?: R.drawable.producto1)
                    .memoryCacheKey(System.currentTimeMillis().toString())
                    .diskCacheKey(System.currentTimeMillis().toString())
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

            // GUARDAR PRODUCTO
            Button(
                onClick = {

                    scope.launch {

                        try {

                            isLoading = true

                            val savedImagePath =
                                selectedUri?.let { uri ->

                                    SupabaseStorageManager.uploadImage(
                                        context,
                                        uri
                                    )
                                }

                            println("URL IMAGEN: $savedImagePath")

                            val productoId =
                                UUID.randomUUID().toString()

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

                            isLoading = false

                            navController.popBackStack()

                        } catch (e: Exception) {

                            isLoading = false

                            e.printStackTrace()

                            println(
                                "ERROR SUBIENDO IMAGEN: ${e.message}"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading &&
                        nombre.isNotBlank() &&
                        codigo.isNotBlank()
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text("Guardando...")

                } else {

                    Text(stringResource(R.string.guardar))
                }
            }

        }
    }
}