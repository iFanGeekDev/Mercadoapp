package com.mercadoapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mercadoapp.domain.model.SelectableOption
import com.mercadoapp.ui.theme.Brand500

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> SelectableChips(
    title: String,
    options: List<SelectableOption<T>>,
    selected: T?,
    toLabel: (T) -> String,
    onSelected: (T) -> Unit
) {
    if (options.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(options) { option ->
                val isSelected = selected == option.value
                val bgColor by animateColorAsState(
                    targetValue = when {
                        isSelected   -> Brand500
                        !option.enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        else         -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    label = "chip_bg"
                )
                val textColor by animateColorAsState(
                    targetValue = when {
                        isSelected     -> Color.White
                        !option.enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                        else           -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    label = "chip_text"
                )

                FilterChip(
                    selected = isSelected,
                    onClick  = { if (option.enabled) onSelected(option.value) },
                    label = {
                        Text(
                            toLabel(option.value),
                            color = textColor,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    enabled = option.enabled,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor         = bgColor,
                        selectedContainerColor = bgColor,
                        disabledContainerColor = bgColor
                    ),
                    border = if (isSelected) null else BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
    }
}
