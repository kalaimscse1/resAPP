package com.warriortech.resb.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Card
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.CircularProgressIndicator
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.warriortech.resb.model.Table
import com.warriortech.resb.ui.theme.BluePrimary
import com.warriortech.resb.ui.theme.TableAvailable
import com.warriortech.resb.ui.theme.TextPrimary
import com.warriortech.resb.util.NetworkStatusBar
import com.warriortech.resb.ui.viewmodel.TableViewModel

/**
 * Screen displaying restaurant tables with offline support
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableScreen(
    onTableSelected: (Table) -> Unit,
    viewModel: TableViewModel = hiltViewModel()
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val tablesState by viewModel.tablesState.collectAsState()
    val area by viewModel.areas.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { androidx.compose.material3.Text("Table Selection") }
        )
    }) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()
            .padding(paddingValues)
            .background(color = BluePrimary)) {
            // Network status bar at top of screen
            NetworkStatusBar(connectionState = connectionState)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp)
                    .padding(horizontal = 16.dp)
                    .background(color = BluePrimary),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                area.listIterator().forEach { area ->
                    if (area.area_name != "--")
                        FilterChip(
                            text = area.area_name,
                            selected = true,
                            onClick = { viewModel.setSection(area.area_id) }
                        )
                }
            }

            // Tables content
            when (tablesState) {
                is TableViewModel.TablesState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is TableViewModel.TablesState.Success -> {
                    val tables = (tablesState as TableViewModel.TablesState.Success).tables

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize().background(color = BluePrimary)
                    ) {
                        items(tables) { table ->
                            TableItem(
                                table = table,
                                onClick = { onTableSelected(table) }
                            )
                        }
                    }
                }

                is TableViewModel.TablesState.Error -> {
                    val errorMessage = (tablesState as TableViewModel.TablesState.Error).message

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TableItem(
    table: Table,
    onClick: () -> Unit
) {
    val backgroundColor = when (table.table_status) {
        "available" -> Color(0xFF4CAF50) // Green
        "occupied" -> Color(0xFFE57373)  // Red
        "reserved" -> Color(0xFFFFB74D)  // Orange
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = 4.dp,
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor.copy(alpha = 0.2f))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = " ${table.table_name}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${table.seating_capacity} Seats",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = table.is_ac,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = table.table_status.capitalize(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (selected) TableAvailable else Color.Gray,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = if (selected) TableAvailable else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (selected) TextPrimary else Color.Gray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}