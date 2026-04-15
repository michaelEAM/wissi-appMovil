package com.wissi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.wissi.ui.navigation.AppNavigation
import com.wissi.ui.theme.Wissi2Theme
import com.wissi.viewmodel.InventoryViewModel
import androidx.activity.viewModels

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Wissi2Theme {
                val viewModel: InventoryViewModel by viewModels()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

