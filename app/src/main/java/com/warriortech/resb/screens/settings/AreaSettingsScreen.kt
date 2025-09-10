package com.warriortech.resb.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DomainAdd
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.viewmodel.AreaViewModel
import com.warriortech.resb.model.Area
import kotlinx.coroutines.launch

/**
 * Screen for managing areas in the application.
 * This screen allows users to view, add, edit, and delete areas.
 * It uses a [Scaffold] to provide a top app bar and a snackbar for messages.
 * The areas are displayed in a [LazyColumn] with each area represented by an [AreaCard].
 * The user can add a new area using an [AddAreaDialog] and edit existing areas with an [EditAreaDialog].
 * Handles loading states and displays progress indicators when necessary.
 * Messages are shown using a [SnackbarHost] to inform the user of success or error states.
 * @param onBackPressed Callback to handle back navigation.
 * @param viewModel The [AreaViewModel] instance to manage the area data.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreaSettingsScreen(
    onBackPressed: () -> Unit,
    viewModel: AreaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingArea by remember { mutableStateOf<Area?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle messages
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Area Settings", color = SurfaceLight) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = SurfaceLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                ),
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Area",
                            tint = SurfaceLight)
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.areas) { area ->
                    AreaCard(
                        area = area,
                        onEdit = { editingArea = area },
                        onDelete = { viewModel.deleteArea(area.area_id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddAreaDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name ->
                viewModel.addArea(name)
                showAddDialog = false
            }
        )
    }

    editingArea?.let { area ->
        EditAreaDialog(
            area = area,
            onDismiss = { editingArea = null },
            onUpdate = { updatedArea ->
                viewModel.updateArea(updatedArea)
                editingArea = null
            }
        )
    }
}

@Composable
fun AreaCard(
    area: Area,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    MobileOptimizedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = area.area_name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAreaDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            // Small drag indicator at the top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Text(
                text = "Add Area",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Text fields
            OutlinedTextField(
                value = name,
                onValueChange = { name = it.uppercase() },
                label = { Text("Area Name") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        onDismiss()
                    }
                }) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            onAdd(name)
                        }
                    },
                    enabled = name.isNotBlank(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add")
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAreaDialog(
    area: Area,
    onDismiss: () -> Unit,
    onUpdate: (Area) -> Unit
) {
    var name by remember { mutableStateOf(area.area_name) }
    var isActive by remember { mutableStateOf(area.isActvice) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Text(
                text = "Edit Area",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Text field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it.uppercase() },
                label = { Text("Area Name") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Active checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isActive,
                    onCheckedChange = { isActive = it }
                )
                Text("Active")
            }

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        onDismiss()
                    }
                }) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            onUpdate(area.copy(area_name = name, isActvice = isActive))
                        }
                    },
                    enabled = name.isNotBlank(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Update")
                }
            }
        }
    }
}
