package com.warriortech.resb.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.model.MenuCategory
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.theme.GradientStart
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.viewmodel.MenuCategorySettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuCategorySettingsScreen(
    onBackPressed: () -> Unit,
    viewModel: MenuCategorySettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<MenuCategory?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MenuCategory Settings", color = SurfaceLight) },
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
                        Icon(Icons.Default.Add, contentDescription = "Add MenuCategory",
                            tint = SurfaceLight)
                    }
                }
            )
        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { showAddDialog = true }
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Add MenuCategory")
//            }
//        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
//            Button(
//                onClick = { showAddDialog = true },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Icon(Icons.Default.Add, contentDescription = null)
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Add Category")
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))

            when (val state=uiState) {
                is MenuCategorySettingsViewModel.UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is MenuCategorySettingsViewModel.UiState.Success -> {
                    if (state.categories.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No categories found", style = MaterialTheme.typography.bodyLarge)
                        }
                        return@Column
                    }else{
                    LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.categories) { category ->
                            CategoryCard(
                                category = category,
                                onEdit = { editingCategory = it },
                                onDelete = {
                                    scope.launch {
                                        viewModel.deleteCategory(it.item_cat_id)
                                    }
                                }
                            )
                        }
                    }
                  }
                }

                is MenuCategorySettingsViewModel.UiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (showAddDialog) {
            CategoryDialog(
                category = null,
                onDismiss = { showAddDialog = false },
                onConfirm = { name, description, sortOrder ->
                    scope.launch {
                        viewModel.addCategory(name, description, sortOrder)
                        showAddDialog = false
                    }
                }
            )
        }

        editingCategory?.let { category ->
            CategoryDialog(
                category = category,
                onDismiss = { editingCategory = null },
                onConfirm = { name, description, sortOrder ->
                    scope.launch {
                        viewModel.updateCategory(category.item_cat_id, name, description, sortOrder)
                        editingCategory = null
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: MenuCategory,
    onEdit: (MenuCategory) -> Unit,
    onDelete: (MenuCategory) -> Unit
) {
    MobileOptimizedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.item_cat_name,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            IconButton(onClick = { onEdit(category) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }

            IconButton(onClick = { onDelete(category) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun CategoryDialog(
    category: MenuCategory?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(category?.item_cat_name ?: "") }
    var orderBy by remember { mutableStateOf(category?.order_by ?: "1") }
    var isActive by remember { mutableStateOf(category?.is_active != false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category == null) "Add Category" else "Edit Category") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.uppercase() },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = orderBy,
                    onValueChange = { orderBy = it },
                    label = { Text("Order") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Active")
                }

            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    onConfirm(name, orderBy, isActive)
                }
            ) {
                Text(if (category == null) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
