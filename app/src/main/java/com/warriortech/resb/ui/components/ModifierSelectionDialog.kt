//
package com.warriortech.resb.ui.components
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.selection.selectable
//import androidx.compose.foundation.selection.selectableGroup
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.window.Dialog
//import com.warriortech.resb.model.MenuItem
//import com.warriortech.resb.model.Modifiers
//import com.warriortech.resb.model.ModifierGroup
//import com.warriortech.resb.model.MenuItemWithModifiers
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ModifierSelectionDialog(
//    menuItem: MenuItem,
//    modifierGroups: List<Modifiers>,
//    onDismiss: () -> Unit,
//    onConfirm: (MenuItemWithModifiers) -> Unit
//) {
//    var selectedModifiers by remember { mutableStateOf<Map<Long, List<Modifiers>>>(emptyMap()) }
//
//    val totalPrice = remember(selectedModifiers) {
//        menuItem.rate + selectedModifiers.values.flatten().sumOf { it.add_on_price }
//    }
//
//    Dialog(onDismissRequest = onDismiss) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .heightIn(max = 500.dp),
//            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            ) {
//                // Header
//                Text(
//                    text = "Customize ${menuItem.menu_item_name}",
//                    style = MaterialTheme.typography.headlineSmall,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(bottom = 16.dp)
//                )
//
//                // Modifier Groups
//                LazyColumn(
//                    modifier = Modifier.weight(1f),
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    item{
//                    ModifierGroupSection(
//                        group = modifierGroups,
//                        selectedModifiers = selectedModifiers[0] ?: emptyList(),
//                        onModifierSelected = { modifier, isSelected ->
//                            val currentSelection = selectedModifiers[0] ?: emptyList()
//                            val newSelection = if (isSelected) {
//                                if (modifier.max_selection == 1) {
//                                    listOf(modifier)
//                                }else{
//                                    if (currentSelection.size < modifier.max_selection) {
//                                        currentSelection + modifier
//                                    }
//                                    else{
//                                        currentSelection
//                                    }
//                                }
//                            }else{
//                                currentSelection - modifier
//                            }
//                            selectedModifiers = selectedModifiers.toMutableMap().apply {
//                                this[0] = newSelection
//                            }
//                        }
//                    )
//                    }
//                }
//
//                Divider(modifier = Modifier.padding(vertical = 16.dp))
//
//                // Price Display
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "Total Price:",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        text = "₹%.2f".format(totalPrice),
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Action Buttons
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    OutlinedButton(
//                        onClick = onDismiss,
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Cancel")
//                    }
//
//                    Button(
//                        onClick = {
//                            val menuItemWithModifiers = MenuItemWithModifiers(
//                                menuItem = menuItem,
////                                availableModifiers = selectedModifiers.values.flatten(),
//                                selectedModifiers = selectedModifiers.values.flatten() ,
//                                totalPrice = totalPrice
//                            )
//                            onConfirm(menuItemWithModifiers)
//                        },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Add to Order")
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun ModifierGroupSection(
//    group:List<Modifiers>,
//    selectedModifiers: List<Modifiers>,
//    onModifierSelected: (Modifiers, Boolean) -> Unit
//) {
//    Column {
////        if (group.max_selection == 1) Modifier.selectableGroup() else Modifier
//        Column(
//            modifier = Modifier
//        ) {
//            group.forEach { modifier ->
//                val isSelected = selectedModifiers.contains(modifier)
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .selectable(
//                            selected = isSelected,
//                            onClick = { onModifierSelected(modifier, !isSelected) }
//                        )
//                        .padding(vertical = 8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    if (group.max_selection == 1) {
//                        RadioButton(
//                            selected = isSelected,
//                            onClick = { onModifierSelected(modifier, !isSelected) }
//                        )
//                    } else {
//                        Checkbox(
//                            checked = isSelected,
//                            onCheckedChange = { onModifierSelected(modifier, it) }
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    Column(modifier = Modifier.weight(1f)) {
//                        Text(
//                            text = modifier.add_on_name,
//                            style = MaterialTheme.typography.bodyMedium
//                        )
//                        if (modifier.add_on_price > 0) {
//                            Text(
//                                text = "+₹%.2f".format(modifier.add_on_price),
//                                style = MaterialTheme.typography.bodySmall,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
