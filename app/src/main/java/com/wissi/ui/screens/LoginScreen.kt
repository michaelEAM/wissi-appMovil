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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wissi.R
import com.wissi.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: InventoryViewModel
) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        // 🔥 TOPBAR CON FLECHA
        TopAppBar(
            title = { Text("Login Administrador") },
            navigationIcon = {
                IconButton(onClick = { 
                    viewModel.logout()
                    usuario = ""
                    contrasena = ""
                    viewModel.clearError()
                    navController.popBackStack(navController.graph.startDestinationId, inclusive = false)
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center


        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(150.dp)
            )
            Text(
                text = "Login Administrador",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 35.dp)
            )

            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text(stringResource(R.string.usuario)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text(stringResource(R.string.contrasena)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            Button(
                onClick = {
                    viewModel.loginAdmin(usuario, contrasena)
                },
                enabled = !state.isLoggingIn
            ) {
                Text(if (state.isLoggingIn) "Verificando..." else "Ingresar")
            }

            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            LaunchedEffect(state.isAdmin, state.error, state.isLoggingIn) {
                if (state.isAdmin && state.error == null && !state.isLoggingIn) {
                    navController.navigate("categorias") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

        }
    }
}

