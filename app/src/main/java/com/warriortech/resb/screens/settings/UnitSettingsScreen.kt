package com.warriortech.resb.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.warriortech.resb.R
import com.warriortech.resb.model.Unit
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.components.ReusableBottomSheet
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.viewmodel.UnitSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitSettingsScreen(
    navController: NavController,
    viewModel: UnitSettingsViewModel = hiltViewModel()
) {
    val units by viewModel.units.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedUnit by remember { mutableStateOf<Unit?>(null) }
    var unitName by remember { mutableStateOf("") }
    var unitSymbol by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadUnits()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unit Settings", color = SurfaceLight) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SurfaceLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedUnit = null
                    unitName = ""
                    unitSymbol = ""
                    showBottomSheet = true
                },
                containerColor = PrimaryGreen
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Unit", tint = SurfaceLight)
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(units) { unit ->
                    MobileOptimizedCard(
                        onClick = {
                            selectedUnit = unit
                            unitName = unit.name
                            unitSymbol = unit.symbol ?: ""
                            showBottomSheet = true
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = unit.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (!unit.symbol.isNullOrEmpty()) {
                                    Text(
                                        text = "Symbol: ${unit.symbol}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    viewModel.deleteUnit(unit.id)
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }

        ReusableBottomSheet(
            isVisible = showBottomSheet,
            onDismiss = { showBottomSheet = false },
            title = if (selectedUnit == null) "Add Unit" else "Edit Unit"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = unitName,
                    onValueChange = { unitName = it },
                    label = { Text("Unit Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = unitSymbol,
                    onValueChange = { unitSymbol = it },
                    label = { Text("Unit Symbol") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { showBottomSheet = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (selectedUnit == null) {
                                viewModel.addUnit(unitName, unitSymbol)
                            } else {
                                viewModel.updateUnit(
                                    selectedUnit!!.copy(
                                        name = unitName,
                                        symbol = unitSymbol
                                    )
                                )
                            }
                            showBottomSheet = false
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text(if (selectedUnit == null) "Add" else "Update")
                    }
                }
            }
        }
    }
}