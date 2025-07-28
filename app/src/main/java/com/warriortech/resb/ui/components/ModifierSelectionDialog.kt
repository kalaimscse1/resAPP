
package com.warriortech.resb.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.Modifier
import com.warriortech.resb.model.ModifierGroup
import com.warriortech.resb.model.MenuItemWithModifiers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifierSelectionDialog(
    menuItem: MenuItem,
    modifierGroups: List<ModifierGroup>,
    onDismiss: () -> Unit,
    onConfirm: (MenuItemWithModifiers) -> Unit
) {
    var selectedModifiers by remember { mutableStateOf<Map<Long, List<Modifier>>>(emptyMap()) }
    
    val totalPrice = remember(selectedModifiers) {
        menuItem.price + selectedModifiers.values.flatten().sumOf { it.price }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Text(
                    text = "Customize ${menuItem.name}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Modifier Groups
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(modifierGroups) { group ->
                        ModifierGroupSection(
                            group = group,
                            selectedModifiers = selectedModifiers[group.modifier_group_id] ?: emptyList(),
                            onModifierSelected = { modifier, isSelected ->
                                val currentSelection = selectedModifiers[group.modifier_group_id] ?: emptyList()
                                val newSelection = if (isSelected) {
                                    if (group.max_selection == 1) {
                                        listOf(modifier)
                                    } else {
                                        if (currentSelection.size < group.max_selection) {
                                            currentSelection + modifier
                                        } else currentSelection
                                    }
                                } else {
                                    currentSelection - modifier
                                }
                                selectedModifiers = selectedModifiers.toMutableMap().apply {
                                    this[group.modifier_group_id] = newSelection
                                }
                            }
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                // Price Display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Price:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "₹%.2f".format(totalPrice),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
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
                            val menuItemWithModifiers = MenuItemWithModifiers(
                                menuItem = menuItem,
                                selectedModifiers = selectedModifiers.values.flatten(),
                                totalPrice = totalPrice
                            )
                            onConfirm(menuItemWithModifiers)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add to Order")
                    }
                }
            }
        }
    }
}

@Composable
private fun ModifierGroupSection(
    group: ModifierGroup,
    selectedModifiers: List<Modifier>,
    onModifierSelected: (Modifier, Boolean) -> Unit
) {
    Column {
        Text(
            text = group.name + if (group.is_required) " *" else "",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (group.max_selection > 1) {
            Text(
                text = "Select up to ${group.max_selection}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Column(
            modifier = if (group.max_selection == 1) Modifier.selectableGroup() else Modifier
        ) {
            group.modifiers.forEach { modifier ->
                val isSelected = selectedModifiers.contains(modifier)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = isSelected,
                            onClick = { onModifierSelected(modifier, !isSelected) }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (group.max_selection == 1) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onModifierSelected(modifier, !isSelected) }
                        )
                    } else {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onModifierSelected(modifier, it) }
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = modifier.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (modifier.price > 0) {
                            Text(
                                text = "+₹%.2f".format(modifier.price),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
