package com.warriortech.resb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.CircularProgressIndicator
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.warriortech.resb.model.Table
import com.warriortech.resb.ui.viewmodel.TableViewModel
import com.warriortech.resb.util.NetworkStatusBar
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ScrollableTabRow
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Tab
import com.warriortech.resb.ui.theme.GradientStart
import com.warriortech.resb.ui.theme.TextPrimary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionScreen(
    onTableSelected: (Table) -> Unit,
    viewModel: TableViewModel = hiltViewModel(),
    drawerState: DrawerState
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val tablesState by viewModel.tablesState.collectAsState()
    val areas by viewModel.areas.collectAsState()
    val scope = rememberCoroutineScope()
    var selectedArea by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        viewModel.loadTables()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selection", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            NetworkStatusBar(connectionState = connectionState)
            if (areas.isNotEmpty()) {
                val displayablAreas = areas.filter { it.area_name != "--" }
                val calculatedIndex = displayablAreas.indexOfFirst { it.area_name == selectedArea }
                ScrollableTabRow(
                    selectedTabIndex = calculatedIndex.coerceAtLeast(0),
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    contentColor = TextPrimary,
                    edgePadding = 0.dp
                ) {
                    displayablAreas.forEachIndexed { index, areaItem ->
                        Tab(
                            selected = areaItem.area_name == selectedArea,
                            onClick = {
                                selectedArea = areaItem.area_name
                                viewModel.setSection(areaItem.area_id)
                            },
                            text = { Text(areaItem.area_name) }
                        )
                    }
                }
            }


            // Table Grid
            when (val currentTablesState = tablesState) {
                is TableViewModel.TablesState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is TableViewModel.TablesState.Success -> {
                    val tables = currentTablesState.tables
                    val filteredTables = if (selectedArea != null) {
                        tables.filter { it.area_name == selectedArea }
                    } else {
                        tables
                    }
                    if (filteredTables.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No tables available for the selected criteria.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            filteredTables.forEach { table ->
                                TableItem(table = table, onClick = { onTableSelected(table) })
                            }
                        }
                    }
                }

                is TableViewModel.TablesState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentTablesState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TableItem(table: Table, onClick: () -> Unit) {
    val color = when (table.table_availability) {
        "AVAILABLE" -> MaterialTheme.colorScheme.primaryContainer
        "OCCUPIED" -> MaterialTheme.colorScheme.errorContainer
        "RESERVED" -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        elevation = 4.dp,
        border = _root_ide_package_.androidx.compose.foundation.BorderStroke(4.dp, color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(table.table_name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("${table.seating_capacity} Seats", style = MaterialTheme.typography.bodySmall)
        }
    }
}