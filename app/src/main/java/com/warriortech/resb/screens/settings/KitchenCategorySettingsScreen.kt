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
import com.warriortech.resb.model.KitchenCategory
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.components.ReusableBottomSheet
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.viewmodel.KitchenCategorySettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenCategorySettingsScreen(
    navController: NavController,
    viewModel: KitchenCategorySettingsViewModel = hiltViewModel()
) {
    val kitchenCategories by viewModel.kitchenCategories.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<KitchenCategory?>(null) }
    var categoryName by remember { mutableStateOf("") }
    var categoryDescription by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadKitchenCategories()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kitchen Category Settings", color = SurfaceLight) },
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
                    selectedCategory = null
                    categoryName = ""
                    categoryDescription = ""
                    showBottomSheet = true
                },
                containerColor = PrimaryGreen
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Category", tint = SurfaceLight)
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
                items(kitchenCategories) { category ->
                    MobileOptimizedCard(
                        onClick = {
                            selectedCategory = category
                            categoryName = category.name
                            categoryDescription = category.description ?: ""
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
                                    text = category.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (!category.description.isNullOrEmpty()) {
                                    Text(
                                        text = category.description,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    viewModel.deleteKitchenCategory(category.id)
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
            title = if (selectedCategory == null) "Add Kitchen Category" else "Edit Kitchen Category"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = categoryDescription,
                    onValueChange = { categoryDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
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
                            if (selectedCategory == null) {
                                viewModel.addKitchenCategory(categoryName, categoryDescription)
                            } else {
                                viewModel.updateKitchenCategory(
                                    selectedCategory!!.copy(
                                        name = categoryName,
                                        description = categoryDescription
                                    )
                                )
                            }
                            showBottomSheet = false
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text(if (selectedCategory == null) "Add" else "Update")
                    }
                }
            }
        }
    }
}