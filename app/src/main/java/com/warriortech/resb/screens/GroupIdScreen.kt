package com.warriortech.resb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.IconButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.warriortech.resb.model.TblGroupDetails
import com.warriortech.resb.model.TblGroupNature
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.ResbTypography
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.viewmodel.GroupDetailsViewModel
import com.warriortech.resb.util.GroupNatureDropdown
import com.warriortech.resb.util.StringDropdown
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    viewModel: GroupDetailsViewModel = hiltViewModel(),
    drawerState: DrawerState,
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingGroup by remember { mutableStateOf<TblGroupDetails?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Column {
                            androidx.compose.material3.Text(
                                "Group Details",
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
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
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
                                "${group.group_name} (${group.group_code})",
                                fontWeight = FontWeight.Bold
                            )
                            Text("Nature: ${group.group_nature.g_nature_name}")
                            Text("Active: ${if (group.is_active) "Yes" else "No"}")
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = {
                                editingGroup = group
                                showDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = { viewModel.deleteGroup(group.group_id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            GroupFormDialog(
                group = editingGroup,
                natures = viewModel.getNatures(),
                onDismiss = { showDialog = false },
                onSave = {
                    if (editingGroup == null)
                        viewModel.addGroup(it)
                    else
                        viewModel.updateGroup(it)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun GroupFormDialog(
    group: TblGroupDetails?,
    natures: List<TblGroupNature>,
    onDismiss: () -> Unit,
    onSave: (TblGroupDetails) -> Unit
) {
    val groupList = listOf("YES", "NO")
    var groupCode by remember { mutableStateOf(group?.group_code ?: "") }
    var groupName by remember { mutableStateOf(group?.group_name ?: "") }
    var groupOrder by remember { mutableStateOf(group?.group_order?.toString() ?: "") }
    var subGroup by remember { mutableStateOf(group?.sub_group ?: "") }
    var grossProfit by remember { mutableStateOf(group?.gross_profit?.toString() ?: "") }
    var tamilText by remember { mutableStateOf(group?.tamil_text ?: "") }
    var groupBy by remember { mutableStateOf(group?.group_by ?: "") }
    var isActive by remember { mutableStateOf(group?.is_active ?: true) }

    var expanded by remember { mutableStateOf(false) }
    var selectedNature by remember { mutableStateOf(group?.group_nature ?: natures.firstOrNull()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (group == null) "Add Group" else "Edit Group") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = groupCode,
                    onValueChange = { groupCode = it },
                    label = { Text("Group Code") })
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name") })
                OutlinedTextField(
                    value = groupOrder,
                    onValueChange = { groupOrder = it },
                    label = { Text("Order") })
                StringDropdown(
                    options = groupList,
                    selectedOption = groupList.find { it == subGroup },
                    onOptionSelected = { subGroup = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Sub Group"
                )
                GroupNatureDropdown(
                    groupNatures = natures,
                    selectedGroupNature = natures.find { it.g_nature_id == selectedNature?.g_nature_id },
                    onGroupNatureSelected = { selectedNature = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Group Nature"
                )
                StringDropdown(
                    options = groupList,
                    selectedOption = groupList.find { it == grossProfit },
                    onOptionSelected = { grossProfit = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Gross Profit Effect"
                )
                OutlinedTextField(
                    value = tamilText,
                    onValueChange = { tamilText = it },
                    label = { Text("Tamil Text") })
                OutlinedTextField(
                    value = groupBy,
                    onValueChange = { groupBy = it },
                    label = { Text("Group") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isActive, onCheckedChange = { isActive = it })
                    Text("Active")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedGroup = TblGroupDetails(
                    group_id = group?.group_id ?: 0,
                    group_code = groupCode,
                    group_name = groupName,
                    group_order = groupOrder.toIntOrNull() ?: 0,
                    sub_group = subGroup,
                    group_nature = selectedNature ?: TblGroupNature(0, "", true),
                    gross_profit = grossProfit,
                    tamil_text = tamilText,
                    is_active = isActive,
                    group_by = groupBy
                )
                onSave(updatedGroup)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
