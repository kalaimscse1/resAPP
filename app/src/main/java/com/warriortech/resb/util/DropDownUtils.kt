package com.warriortech.resb.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.warriortech.resb.model.Area
import com.warriortech.resb.model.Counters
import com.warriortech.resb.model.Tax
import com.warriortech.resb.model.TblCounter
import kotlin.collections.forEach


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreaDropdown(
    areas: List<Area>,
    selectedArea: Area?,
    onAreaSelected: (Area) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Select Area"
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField( // Or TextField if you prefer a different style
            value = selectedArea?.area_name ?: "", // Display selected area name or empty
            onValueChange = {}, // Not directly editable, selection happens via dropdown
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor() // Important: This anchors the dropdown menu
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (areas.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No areas available") },
                    onClick = {
                        expanded = false
                    },
                    enabled = false // Disable if no areas
                )
            } else {
                areas.forEach { area ->
                    DropdownMenuItem(
                        text = { Text(area.area_name) },
                        onClick = {
                            onAreaSelected(area)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxDropdown(
    taxes: List<Tax>,
    selectedTax: Tax?,
    onTaxSelected: (Tax) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Select Tax"
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField( // Or TextField if you prefer a different style
            value = selectedTax?.tax_name ?: "", // Display selected area name or empty
            onValueChange = {}, // Not directly editable, selection happens via dropdown
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor() // Important: This anchors the dropdown menu
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (taxes.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No Taxes available") },
                    onClick = {
                        expanded = false
                    },
                    enabled = false // Disable if no areas
                )
            } else {
                taxes.forEach { tax ->
                    DropdownMenuItem(
                        text = { Text(tax.tax_name) },
                        onClick = {
                            onTaxSelected(tax)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterDropdown(
    counters: List<TblCounter>,
    selectedCounter: TblCounter?,
    onCounterSelected: (TblCounter) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Select Counter"
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField( // Or TextField if you prefer a different style
            value = selectedCounter?.counter_name ?: "", // Display selected area name or empty
            onValueChange = {}, // Not directly editable, selection happens via dropdown
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor() // Important: This anchors the dropdown menu
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (counters.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No Counter available") },
                    onClick = {
                        expanded = false
                    },
                    enabled = false // Disable if no areas
                )
            } else {
                counters.forEach { counter ->
                    DropdownMenuItem(
                        text = { Text(counter.counter_name) },
                        onClick = {
                            onCounterSelected(counter)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StringDropdown(
    options: List<String>,
    selectedOption: String?, // The currently selected string
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Select an Option"
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption ?: "", // Display selected option or empty
            onValueChange = {}, // Not directly editable
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor() // Anchor the dropdown menu
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (options.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No options available") },
                    onClick = {
                        expanded = false
                    },
                    enabled = false
                )
            } else {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}