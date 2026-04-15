package com.wissi.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wissi.R

@Composable
fun InicioScreen(
    navController: NavController
) {
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
            modifier = Modifier.size(200.dp)
        )
        
        Text(
            text = "DETALLES WISSI",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Gestión de Inventarios",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        Button(
            onClick = { navController.navigate("principal") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.ingresar),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

