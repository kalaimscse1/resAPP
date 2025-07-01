package com.warriortech.resb.screens


import android.graphics.Color.rgb
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.warriortech.resb.model.Table
import com.warriortech.resb.ui.viewmodel.TableViewModel
import com.warriortech.resb.util.NetworkStatusBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionScreen(
    onDinePressed: () -> Unit,
    onTakeAwayPressed: () -> Unit,
    onDeliverPressed: () -> Unit,
    onTableSelected: (Table) -> Unit,
    viewModel: TableViewModel = hiltViewModel(),
    drawerState: DrawerState
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val tablesState by viewModel.tablesState.collectAsState()
    val areas by viewModel.areas.collectAsState()
    val scope = rememberCoroutineScope()

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
                    containerColor = MaterialTheme.colorScheme.surface
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

            // Order Type Chips
//            Surface(
//                modifier = Modifier.padding(16.dp),
//                elevation = 1.dp,
//                shape = MaterialTheme.shapes.medium
//            ) {
//                FlowRow(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    AssistChip(
//                        onClick = onDinePressed,
//                        label = { Text("Dine-In") }
//                    )
//                    AssistChip(
//                        onClick = onTakeAwayPressed,
//                        label = { Text("TakeAway") }
//                    )
//                    AssistChip(
//                        onClick = onDeliverPressed,
//                        label = { Text("Delivery") }
//                    )
//                }
//            }

            // Area Chips
            if (areas.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    areas.filter { it.area_name != "--" }.forEach { areaItem ->
                        AssistChip(
                            onClick = { viewModel.setSection(areaItem.area_id)
                                Modifier.background(Color(rgb(129, 154, 145)))},
                            label = { Text(areaItem.area_name) }
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
                    if (tables.isEmpty()) {
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
                            tables.forEach { table ->
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
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp,
        color = color
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
