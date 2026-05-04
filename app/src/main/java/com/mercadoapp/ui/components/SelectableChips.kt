package com.mercadoapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mercadoapp.domain.model.SelectableOption

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> SelectableChips(
    title: String,
    options: List<SelectableOption<T>>,
    selected: T?,
    toLabel: (T) -> String,
    onSelected: (T) -> Unit
) {
    Text(title)
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = selected == option.value,
                enabled = option.enabled,
                onClick = { onSelected(option.value) },
                label = { Text(toLabel(option.value)) }
            )
        }
    }
}
