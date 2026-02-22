package com.blac.ai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blac.ai.data.ToggleOptions

@Composable
fun OptionsBar(
    options: ToggleOptions,
    onToggleThink: () -> Unit,
    onToggleSearch: () -> Unit,
    onToggleCode: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = options.thinkMode,
            onClick = onToggleThink,
            label = { Text("Think") },
            leadingIcon = if (options.thinkMode) {
                { Icon(Icons.Default.Check, null) }
            } else null
        )
        FilterChip(
            selected = options.realTimeSearch,
            onClick = onToggleSearch,
            label = { Text("Search") }
        )
        FilterChip(
            selected = options.codeMode,
            onClick = onToggleCode,
            label = { Text("Code") }
        )
    }
}