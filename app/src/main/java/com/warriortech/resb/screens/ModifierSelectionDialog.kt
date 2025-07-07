
package com.warriortech.resb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.warriortech.resb.model.Modifiers
import com.warriortech.resb.model.ModifierType
import kotlin.collections.any
import kotlin.collections.filter
import kotlin.collections.plus
import kotlin.text.isNotEmpty

@Composable
fun ModifierSelectionDialog(
    availableModifiers: List<Modifiers>,
    selectedModifiers: List<Modifiers>,
    onModifiersSelected: (List<Modifiers>) -> Unit,
    onDismiss: () -> Unit
) {
    var currentSelectedModifiers by remember { mutableStateOf(selectedModifiers) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Modifiers",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableModifiers) { modifier ->
                        ModifierItem(
                            modifier = modifier,
                            isSelected = currentSelectedModifiers.any { it.modifier_id == modifier.modifier_id },
                            onSelectionChanged = { isSelected ->
                                currentSelectedModifiers = if (isSelected) {
                                    currentSelectedModifiers + modifier
                                } else {
                                    currentSelectedModifiers.filter { it.modifier_id != modifier.modifier_id }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onModifiersSelected(currentSelectedModifiers)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
private fun ModifierItem(
    modifier: Modifiers,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = { onSelectionChanged(!isSelected) }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChanged
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = modifier.modifier_name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                if (modifier.modifier_name_tamil.isNotEmpty()) {
                    Text(
                        text = modifier.modifier_name_tamil,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val typeText = when (modifier.modifier_type) {
                        ModifierType.ADDITION -> "Addition"
                        ModifierType.REMOVAL -> "Remove"
                        ModifierType.SUBSTITUTION -> "Substitute"
                    }

                    Text(
                        text = typeText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    if (modifier.price_adjustment != 0.0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (modifier.price_adjustment > 0)
                                "+₹${modifier.price_adjustment}"
                            else
                                "₹${modifier.price_adjustment}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (modifier.price_adjustment > 0)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
