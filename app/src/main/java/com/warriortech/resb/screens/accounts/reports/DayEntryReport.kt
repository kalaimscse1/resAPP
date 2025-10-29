package com.warriortech.resb.screens.accounts.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.CircularProgressIndicator
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SecondaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.viewmodel.DayEntryReportViewmodel
import com.warriortech.resb.util.CurrencySettings
import com.warriortech.resb.util.LedgerDropdown
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayEntryReportScreen(
    viewModel: DayEntryReportViewmodel = hiltViewModel(),
    drawerState: DrawerState,
){
    val ledgerDetailsState by viewModel.ledgerDetailsState.collectAsStateWithLifecycle()
    val ledgerList by viewModel.ledgerList.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var id by remember { mutableStateOf<Long>(1) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.material.Text(
                        "Ledger Reports",
                        style = MaterialTheme.typography.titleLarge,
                        color = SurfaceLight
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
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
    ){ paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ){
                LedgerDropdown(
                    ledgers = ledgerList,
                    selectedLedger = ledgerList.find { it.ledger_id.toLong() == id },
                    onLedgerSelected = {
                        id = it.ledger_id.toLong()
                        viewModel.loadData(id)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Select Ledger"
                )
            }
            when(val state = ledgerDetailsState){
                is DayEntryReportViewmodel.DayEntryUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is DayEntryReportViewmodel.DayEntryUiState.Success -> {
                    LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)){
                        stickyHeader {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(scrollState)
                                    .background(SecondaryGreen) // keeps header visible
                                    .padding(8.dp)
                            ) {
                                Text(
                                    "DAY ENTRY NO",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.width(80.dp)
                                )
                                Text(
                                    "LEDGER NAME",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    "DATE",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    "TIME",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    "REMARKS",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.width(140.dp)
                                )
                                Text(
                                    "DEBIT",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    "CREDIT",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.width(100.dp)
                                )
                            }
                        }
                        items(state.ledgers) { ledger ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(scrollState) // same scroll state as header
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                Text(ledger.member_id, modifier = Modifier.width(80.dp))
                                Text(ledger.ledger.ledger_name, modifier = Modifier.width(100.dp))
                                Text(ledger.date, modifier = Modifier.width(100.dp))
                                Text(ledger.time, modifier = Modifier.width(100.dp))
                                Text(ledger.purpose, modifier = Modifier.width(140.dp))
                                Text(
                                    CurrencySettings.formatPlain(ledger.amount_out),
                                    modifier = Modifier.width(100.dp),
                                    color = if (ledger.amount_out > 0) Color.Red else Color.Black
                                )
                                Text(
                                    CurrencySettings.formatPlain(ledger.amount_in),
                                    modifier = Modifier.width(100.dp),
                                    color = if (ledger.amount_in > 0) SecondaryGreen else Color.Black
                                )

                            }
                        }

                    }
                }
                is DayEntryReportViewmodel.DayEntryUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error: ${state.message}", color = Color.Red)
                    }
                }
            }
        }
    }
}