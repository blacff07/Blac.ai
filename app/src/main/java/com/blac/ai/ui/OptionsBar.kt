package com.blac.ai.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = options.thinkMode,
            onClick = onToggleThink,
            label = { Text("Think", fontSize = 12.sp) },
            leadingIcon = if (options.thinkMode) {
                { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
            } else null
        )
        FilterChip(
            selected = options.realTimeSearch,
            onClick = onToggleSearch,
            label = { Text("Search", fontSize = 12.sp) },
            leadingIcon = if (options.realTimeSearch) {
                { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
            } else null
        )
        FilterChip(
            selected = options.codeMode,
            onClick = onToggleCode,
            label = { Text("Code", fontSize = 12.sp) },
            leadingIcon = if (options.codeMode) {
                { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
            } else null
        )
    }
}