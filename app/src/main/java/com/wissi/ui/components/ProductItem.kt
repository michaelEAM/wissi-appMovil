package com.wissi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.wissi.R
import com.wissi.data.model.Producto
import com.wissi.utils.ImageUtils
import com.wissi.viewmodel.InventoryViewModel
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItem(
    producto: Producto,
    viewModel: InventoryViewModel,
    navController: NavController,
    showAdminActions: Boolean,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD) // color de lso botones
        ),
        onClick = {
            viewModel.selectProducto(producto)
            navController.navigate("detalle/${producto.id}")
        }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        if (producto.imagenPath != null) {
                            ImageUtils.getImageUri(
                                LocalContext.current,
                                producto.imagenPath
                            )
                        } else producto.imagenResId
                    )
                    .crossfade(true)
                    .build(),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.producto1),
                error = painterResource(R.drawable.producto1)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(text = "Código: ${producto.codigo}")
                Text(text = "Precio: $${producto.precio.toInt()}")

                Text(
                    text = stringResource(
                        R.string.cantidad_disponible,
                        producto.cantidad,
                        if (producto.cantidad == 1) "" else "es"
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (producto.cantidad <= 5)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (showAdminActions) {
                Column {

                    IconButton(
                        onClick = {
                            viewModel.selectProducto(producto)
                            navController.navigate("editar_producto/${producto.id}")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color(0xFF4CAF50)
                        )

                    }

                    IconButton(
                        onClick = {
                            viewModel.deleteProducto(producto.id)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar",
                            tint = Color(0xFFFF6B6B)
                        )
                    }
                }
            }
        }
    }
}
