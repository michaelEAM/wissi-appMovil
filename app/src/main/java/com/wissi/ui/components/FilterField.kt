package com.wissi.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.wissi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = stringResource(id = R.string.buscar)
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(hint) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions.Default,
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}

