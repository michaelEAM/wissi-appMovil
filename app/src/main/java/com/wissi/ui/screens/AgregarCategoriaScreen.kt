package com.wissi.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wissi.R
import com.wissi.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarCategoriaScreen(
    navController: NavController,
    viewModel: InventoryViewModel
) {
    var id by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {

        // 🔥 TOPBAR CON FLECHA DE VOLVER
        TopAppBar(
            title = { Text(stringResource(R.string.agregar_categoria)) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {

            // 🔵 CONTENIDO PRINCIPAL
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                TopAppBar(
                    title = { Text(stringResource(R.string.agregar_categoria)) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("ID") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.addCategoria(id, nombre)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = id.isNotBlank() && nombre.isNotBlank()
                ) {
                    Text(stringResource(R.string.guardar))
                }

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.cancelar))
                }
            }

            // 🔥 LOGO EN LA ESQUINA (AHORA SÍ FUNCIONA)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.TopEnd) // 🔥 ESTA ES LA CLAVE
                    .padding(8.dp)
            )
        }
    }
}