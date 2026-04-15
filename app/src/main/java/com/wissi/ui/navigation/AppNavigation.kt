package com.wissi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wissi.ui.screens.*
import com.wissi.viewmodel.InventoryViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: InventoryViewModel,
    startDestination: String = "inicio"
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable("inicio") {
            InicioScreen(navController)
        }

        composable("principal") {
            PrincipalScreen(navController, viewModel)
        }

        composable("login") {
            LoginScreen(navController, viewModel)
        }

        composable("productos") {
            ProductosScreen(navController, viewModel)
        }

        composable("detalle/{productoId}") { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId") ?: ""
            DetalleProductoScreen(navController, productoId, viewModel)
        }

        composable("categorias") {
            CategoriasScreen(navController, viewModel)
        }

        composable("agregar_categoria") {
            AgregarCategoriaScreen(navController, viewModel)
        }

        composable("productos_categoria/{categoriaId}") { backStackEntry ->
            val categoriaId = backStackEntry.arguments?.getString("categoriaId") ?: ""
            ProductosCategoriaScreen(navController, categoriaId, viewModel)
        }

        composable("agregar_producto/{categoriaId}") { backStackEntry ->
            val categoriaId = backStackEntry.arguments?.getString("categoriaId") ?: ""
            AgregarProductoScreen(navController, categoriaId, viewModel)
        }

        composable("editar_producto/{productoId}") { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId") ?: ""
            EditarProductoScreen(navController, productoId, viewModel)
        }
    }
}