package com.warriortech.resb.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.CircularProgressIndicator
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
import com.warriortech.resb.ui.theme.SuccessGreen
import com.warriortech.resb.ui.theme.TextPrimary
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.warriortech.resb.model.TableStatusResponse
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.ui.theme.DarkGreen
import com.warriortech.resb.ui.theme.DarkRed
import com.warriortech.resb.ui.theme.ErrorRed
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SecondaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.theme.TextSecondary
import com.warriortech.resb.ui.theme.ghostWhite


//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionScreen(
    onTableSelected: (Table) -> Unit,
    viewModel: TableViewModel = hiltViewModel(),
    drawerState: DrawerState,
    sessionManager: SessionManager
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val tablesState by viewModel.tablesState.collectAsState()
    val areas by viewModel.areas.collectAsState()
    val scope = rememberCoroutineScope()

    val displayableAreas = areas.filter { it.area_name != "--" }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { displayableAreas.size }
    )
    val currentArea = displayableAreas.getOrNull(pagerState.currentPage)
    val role = sessionManager.getUser()?.role ?: ""
    val areaId = currentArea?.area_name ?: ""
    var selectedArea by remember { mutableStateOf<String?>(areaId) }
    LaunchedEffect(Unit) {
        viewModel.loadTables()
    }

    // Update selected area and section whenever the page changes
    LaunchedEffect(pagerState.currentPage) {
        currentArea?.let {
            viewModel.setSection(it.area_id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Selection",
                        style = MaterialTheme.typography.titleLarge,
                        color = SurfaceLight
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        androidx.compose.material.Icon(
                            Icons.Default.Menu, contentDescription = "Menu",
                            tint = SurfaceLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NetworkStatusBar(connectionState = connectionState)
            if (role == "ADMIN" || role == "RESBADMIN" || role == "CHEF") {
                if (displayableAreas.isNotEmpty()) {
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        backgroundColor = SecondaryGreen,
                        contentColor = SurfaceLight,
                        edgePadding = 0.dp
                    ) {
                        displayableAreas.forEachIndexed { index, areaItem ->
                            Tab(
                                selected = index == pagerState.currentPage,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = { Text(areaItem.area_name) }

                            )
                        }
                    }

                    HorizontalPager(
                        pageSize = PageSize.Fill,
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val area = displayableAreas[page]
                        val filteredTables = when (val state = tablesState) {
                            is TableViewModel.TablesState.Success ->
                                state.tables.filter { it.area_name == area.area_name }

                            else -> emptyList()
                        }

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
                                if (filteredTables.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No tables in ${area.area_name}.",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                } else {
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(3),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(filteredTables) { table ->
                                            TableItem(
                                                table = table,
                                                onClick = {
                                                    val tbl = Table(
                                                        table_id = table.table_id,
                                                        area_id = table.area_id,
                                                        area_name = table.area_name,
                                                        table_name = table.table_name,
                                                        seating_capacity = table.seating_capacity.toInt(),
                                                        is_ac = table.is_ac,
                                                        table_status = table.table_status,
                                                        table_availability = table.table_availability,
                                                        is_active = table.is_active
                                                    )
                                                    onTableSelected(tbl)
                                                },
                                                sessionManager
                                            )
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
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                if (areas.isNotEmpty()) {
                    val displayablAreas = areas.filter { it.area_name == areaId }
                    val calculatedIndex =
                        displayablAreas.indexOfFirst { it.area_name == selectedArea }
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
                        val filteredTables = when (val state = tablesState) {
                            is TableViewModel.TablesState.Success ->
                                state.tables.filter { it.area_name == areaId }

                            else -> emptyList()
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

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3), // 👈 exactly 3 columns per row
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(filteredTables) { table ->
                                    TableItem(
                                        table = table, onClick = {
                                            val tbl = Table(
                                                table_id = table.table_id,
                                                area_id = table.area_id,
                                                area_name = table.area_name,
                                                table_name = table.table_name,
                                                seating_capacity = table.seating_capacity.toInt(),
                                                is_ac = table.is_ac,
                                                table_status = table.table_status,
                                                table_availability = table.table_availability,
                                                is_active = table.is_active
                                            )
                                            onTableSelected(tbl)
                                        },
                                        sessionManager
                                    )
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
}

@Composable
fun TableItem(table: TableStatusResponse, onClick: () -> Unit, sessionManager: SessionManager) {
    val color = when (table.table_availability) {
        "AVAILABLE" -> TextSecondary
        "OCCUPIED" -> SuccessGreen
        "RESERVED" -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val borderColor: Color = color
    val cornerRadius: Dp = 12.dp
    val borderWidth: Dp = 6.dp

    Surface(
        modifier = Modifier
            .width(90.dp)
            .height(90.dp)
            .clickable(onClick = onClick)
            .border(1.dp, color = borderColor, shape = RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 8.dp,
        color = ghostWhite
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(cornerRadius))
                .drawWithContent {
                    drawContent()

                    val stroke = borderWidth.toPx()
                    val width = size.width
                    val lineWidth = width

                    // Draw animated top border with rounded corners
                    drawRoundRect(
                        color = borderColor,
                        topLeft = Offset(0f, 0f),
                        size = Size(lineWidth, stroke),
                        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
                    )
                }
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = table.table_name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${table.seating_capacity} Seats",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (table.grandTotal > 0) {
                    Text(
                        text = sessionManager.getRestaurantProfile()?.currency + " " + table.grandTotal,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed
                    )
                } else {
                    Text(
                        text = "New",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                }
            }
        }
    }
}