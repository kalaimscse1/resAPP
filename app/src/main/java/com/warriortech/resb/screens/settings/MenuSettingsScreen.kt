package com.warriortech.resb.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.model.Menu
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.viewmodel.MenuSettingsViewModel
import com.warriortech.resb.util.ReusableBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuSettingsScreen(
    viewModel: MenuSettingsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val order by viewModel.orderBy.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMenu by remember { mutableStateOf<Menu?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.getOrderBy()
        viewModel.loadMenus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menu Settings", color = SurfaceLight) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = SurfaceLight
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showAddDialog = true
                    }) {
                        Icon(
                            Icons.Default.Add, contentDescription = "Add Menu",
                            tint = SurfaceLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        },

        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = uiState) {
            is MenuSettingsViewModel.UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MenuSettingsViewModel.UiState.Success -> {
                if (state.menus.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No menus available. Please add a menu.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    )  {
                        items(state.menus) { menu ->
                            MenuCard(
                                menu = menu,
                                onEdit = { editingMenu = it },
                                onDelete = {
                                    scope.launch {
                                        viewModel.deleteMenu(it.menu_id)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            is MenuSettingsViewModel.UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Show error message
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (showAddDialog) {
            viewModel.getOrderBy()
            MenuDialog(
                menu = null,
                onDismiss = { showAddDialog = false },
                onConfirm = { menu ->
                    scope.launch {
                        viewModel.addMenu(
                            menu.menu_name,
                            menu.order_by,
                            menu.is_active,
                            menu.start_time,
                            menu.end_time
                        )
                        showAddDialog = false
                    }
                },
                order = order
            )
        }

        editingMenu?.let { menu ->
            MenuDialog(
                menu = menu,
                onDismiss = { editingMenu = null },
                onConfirm = { menu ->
                    scope.launch {
                        viewModel.updateMenu(
                            menu.menu_id,
                            menu.menu_name,
                            menu.order_by,
                            menu.is_active,
                            menu.start_time,
                            menu.end_time
                        )
                        editingMenu = null
                    }
                },
                order = order
            )
        }
    }
}

@Composable
fun MenuCard(
    menu: Menu,
    onEdit: (Menu) -> Unit,
    onDelete: (Menu) -> Unit
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
                    text = menu.menu_name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = menu.order_by,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (menu.is_active) "Active" else "Inactive",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (menu.is_active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            IconButton(onClick = { onEdit(menu) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }

            IconButton(onClick = { onDelete(menu) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun MenuDialog(
    menu: Menu?,
    onDismiss: () -> Unit,
    onConfirm: (Menu) -> Unit,
    order: String
) {

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val nameFocus = remember { FocusRequester() }
    val descriptionFocus = remember { FocusRequester() }
    val startTimeFocus = remember { FocusRequester() }
    val endTimeFocus = remember { FocusRequester() }
    val isActiveFocus = remember { FocusRequester() }

    val nameBringIntoView = remember { BringIntoViewRequester() }
    val descriptionBringIntoView = remember { BringIntoViewRequester() }
    val startTimeBringIntoView = remember { BringIntoViewRequester() }
    val endTimeBringIntoView = remember { BringIntoViewRequester() }

    var name by remember { mutableStateOf(menu?.menu_name ?: "") }
    var description by remember { mutableStateOf(menu?.order_by ?: order) }
    var isActive by remember { mutableStateOf(menu?.is_active ?: true) }
    var startTime by remember { mutableStateOf(menu?.start_time?.toString()?:"0.0") }
    var endTime by remember { mutableStateOf(menu?.end_time?.toString()?:"0.0") }

    ReusableBottomSheet(
        onDismiss = onDismiss,
        title = if (menu == null) "Add Menu" else "Edit Menu",
        onSave = {
            val menu = Menu(
                menu_id = menu?.menu_id ?: 0L,
                menu_name = name,
                order_by = description,
                start_time = startTime.toFloat(),
                end_time = endTime.toFloat(),
                is_active = isActive
            )
            onConfirm(menu)
        },
        isSaveEnabled = name.isNotBlank() && description.isNotBlank(),
        buttonText = if (menu == null) "Add" else "Update"
    ) {
        Column {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it.uppercase() },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nameFocus)
                    .bringIntoViewRequester(nameBringIntoView),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        descriptionFocus.requestFocus()
                    }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it.uppercase() },
                label = { Text("OrderBy") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(descriptionFocus)
                    .bringIntoViewRequester(descriptionBringIntoView),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        startTimeFocus.requestFocus()
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Menu Time Settings",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = startTime,
                onValueChange = { startTime = it },
                label = { Text("Start Time") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(startTimeFocus)
                    .bringIntoViewRequester(startTimeBringIntoView),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        endTimeFocus.requestFocus()
                    }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = endTime,
                onValueChange = { endTime = it },
                label = { Text("End Time") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(endTimeFocus)
                    .bringIntoViewRequester(endTimeBringIntoView),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        isActiveFocus.requestFocus()
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

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
    }
}
