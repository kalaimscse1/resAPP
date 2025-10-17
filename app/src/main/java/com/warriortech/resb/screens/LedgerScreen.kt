package com.warriortech.resb.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.model.TblLedgerDetails
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.viewmodel.LedgerViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerScreen(
    viewModel: LedgerViewModel = hiltViewModel(),
    drawerState: DrawerState,
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingGroup by remember { mutableStateOf<TblLedgerDetails?>(null) }
    val scope = rememberCoroutineScope()
    val ledgerState by viewModel.ledgerState.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.loadLedgers()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Column {
                            androidx.compose.material3.Text(
                                "Ledger Details",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceLight
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = SurfaceLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingGroup = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        when(val state = ledgerState) {
            is LedgerViewModel.LedgerUiState.Loading -> {
                // Show loading indicator)
                Text("Loading...", modifier = Modifier.padding(paddingValues).padding(16.dp))
            }

            is LedgerViewModel.LedgerUiState.Error -> {
                // Show error message
                Text(
                    "Error: ${state.message}",
                    modifier = Modifier.padding(paddingValues).padding(16.dp)
                )
            }

            is LedgerViewModel.LedgerUiState.Success -> {
                val ledgers = state.ledgers
                if (ledgers.isEmpty()) {
                    Text(
                        "No ledger details found.",
                        modifier = Modifier.padding(paddingValues).padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(viewModel.groups) { group ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            "${group.ledger_code} (${group.ledger_name})",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(group.ledger_group)
                                        Text(if (group.is_active) "Yes" else "No")
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(onClick = {
                                            editingGroup = group
                                            showDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                                        }
//                            IconButton(onClick = { viewModel.deleteGroup(group.group_id) }) {
//                                Icon(Icons.Default.Delete, contentDescription = "Delete")
//                            }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
//        if (showDialog) {
//            GroupFormDialog(
//                group = editingGroup,
//                natures = viewModel.getNatures(),
//                onDismiss = { showDialog = false },
//                onSave = {
//                    if (editingGroup == null)
//                        viewModel.addGroup(it)
//                    else
//                        viewModel.updateGroup(it)
//                    showDialog = false
//                }
//            )
//        }

    }

}