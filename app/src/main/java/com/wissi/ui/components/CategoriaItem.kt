package com.wissi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wissi.R
import com.wissi.data.model.Categoria
import com.wissi.viewmodel.InventoryViewModel
import androidx.compose.ui.graphics.Color
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaItem(
    categoria: Categoria,
    viewModel: InventoryViewModel,
    navController: NavController,
    isAdmin: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD) // azul pastel 🧸
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = { navController.navigate("productos_categoria/${categoria.id}") }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = categoria.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${stringResource(R.string.productos_categoria, categoria.nombre)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))

            if (isAdmin) {
                Button(
                    onClick = { viewModel.deleteCategoria(categoria.id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            }
        }
    }
}

