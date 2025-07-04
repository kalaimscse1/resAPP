
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.MenuItemRepository
import com.warriortech.resb.data.repository.OrderRepository
import com.warriortech.resb.data.repository.TableRepository
import com.warriortech.resb.data.repository.SettingsRepository
import com.warriortech.resb.model.*
import com.warriortech.resb.screens.SettingsItem
import com.warriortech.resb.screens.SettingsModule
import com.warriortech.resb.screens.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _selectedModule = MutableStateFlow<SettingsModule?>(null)
    val selectedModule: StateFlow<SettingsModule?> = _selectedModule.asStateFlow()

    fun loadModuleData(module: SettingsModule) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            try {
                val items = when (module) {
                    is SettingsModule.Area -> {
                        val areas = settingsRepository.getAllAreas()
                        areas.map { area ->
                            SettingsItem(
                                id = area.area_id.toString(),
                                name = area.area_name,
                                description = " ${area.area_name}",
                                data = mapOf(
                                    "name" to area.area_name,
                                    "status" to area.isActvice.toString()
                                )
                            )
                        }
                    }
                    is SettingsModule.Table -> {
                        val tables = settingsRepository.getAllTables()
                        tables.map { table ->
                            SettingsItem(
                                id = table.table_id.toString(),
                                name = table.table_name,
                                description = "Area: ${table.area_name}",
                                data = mapOf(
                                    "name" to table.table_name,
                                    "area_id" to table.area_id.toString(),
                                    "capacity" to table.seating_capacity.toString(),
                                    "status" to table.table_availability
                                )
                            )
                        }
                    }
//                    is SettingsModule.Menu -> {
//                        val menus = settingsRepository.getAllMenus()
//                        menus.map { menu ->
//                            SettingsItem(
//                                id = menu.id.toString(),
//                                name = menu.name,
//                                description = menu.description,
//                                data = mapOf(
//                                    "name" to menu.name,
//                                    "description" to menu.description,
//                                    "is_active" to menu.isActive.toString()
//                                )
//                            )
//                        }
//                    }
//                    is SettingsModule.MenuCategory -> {
//                        val categories = settingsRepository.getAllMenuCategories()
//                        categories.map { category ->
//                            SettingsItem(
//                                id = category.id.toString(),
//                                name = category.name,
//                                description = category.description,
//                                data = mapOf(
//                                    "name" to category.name,
//                                    "description" to category.description,
//                                    "sort_order" to category.sortOrder.toString()
//                                )
//                            )
//                        }
//                    }
//                    is SettingsModule.MenuItem -> {
//                        val items = settingsRepository.getAllMenuItems()
//                        items.map { item ->
//                            SettingsItem(
//                                id = item.menu_item_id.toString(),
//                                name = item.menu_item_name,
//                                description = "Rate: ₹${item.rate}",
//                                data = mapOf(
//                                    "name" to item.menu_item_name,
//                                    "rate" to item.rate.toString(),
//                                    "category" to item.item_cat_name,
//                                    "is_available" to item.is_available
//                                )
//                            )
//                        }
//                    }
//                    is SettingsModule.Customer -> {
//                        val customers = settingsRepository.getAllCustomers()
//                        customers.map { customer ->
//                            SettingsItem(
//                                id = customer.id.toString(),
//                                name = customer.name,
//                                description = customer.phone,
//                                data = mapOf(
//                                    "name" to customer.name,
//                                    "phone" to customer.phone,
//                                    "email" to customer.email,
//                                    "address" to customer.address
//                                )
//                            )
//                        }
//                    }
//                    is SettingsModule.Staff -> {
//                        val staff = settingsRepository.getAllStaff()
//                        staff.map { staffMember ->
//                            SettingsItem(
//                                id = staffMember.id.toString(),
//                                name = staffMember.name,
//                                description = "Role: ${staffMember.role}",
//                                data = mapOf(
//                                    "name" to staffMember.name,
//                                    "role" to staffMember.role,
//                                    "phone" to staffMember.phone,
//                                    "email" to staffMember.email
//                                )
//                            )
//                        }
//                    }
//                    is SettingsModule.Role -> {
//                        val roles = settingsRepository.getAllRoles()
//                        roles.map { role ->
//                            SettingsItem(
//                                id = role.id.toString(),
//                                name = role.name,
//                                description = role.description,
//                                data = mapOf(
//                                    "name" to role.name,
//                                    "description" to role.description,
//                                    "permissions" to role.permissions
//                                )
//                            )
//                        }
//                    }
//                    is SettingsModule.Printer -> {
//                        val printers = settingsRepository.getAllPrinters()
//                        printers.map { printer ->
//                            SettingsItem(
//                                id = printer.id.toString(),
//                                name = printer.name,
//                                description = "IP: ${printer.ipAddress}",
//                                data = mapOf(
//                                    "name" to printer.name,
//                                    "ip_address" to printer.ipAddress,
//                                    "port" to printer.port.toString(),
//                                    "type" to printer.type
//                                )
//                            )
//                        }
//                    }
//                    is SettingsModule.Tax -> {
//                        val taxes = settingsRepository.getAllTaxes()
//                        taxes.map { tax ->
//                            SettingsItem(
//                                id = tax.id.toString(),
//                                name = tax.name,
//                                description = "Rate: ${tax.rate}%",
//                                data = mapOf(
//                                    "name" to tax.name,
//                                    "rate" to tax.rate.toString(),
//                                    "type" to tax.type,
//                                    "is_active" to tax.isActive.toString()
//                                )
//                            )
//                        }
//                    }
//                    is SettingsModule.TaxSplit -> {
//                        val taxSplits = settingsRepository.getAllTaxSplits()
//                        taxSplits.map { taxSplit ->
//                            SettingsItem(
//                                id = taxSplit.id.toString(),
//                                name = taxSplit.name,
//                                description = taxSplit.description,
//                                data = mapOf(
//                                    "name" to taxSplit.name,
//                                    "description" to taxSplit.description,
//                                    "split_type" to taxSplit.splitType,
//                                    "percentage" to taxSplit.percentage.toString()
//                                )
//                            )
//                        }
//                    }
//                    is SettingsModule.RestaurantProfile -> {
//                        val profile = settingsRepository.getRestaurantProfile()
//                        if (profile != null) {
//                            listOf(
//                                SettingsItem(
//                                    id = "1",
//                                    name = profile.name,
//                                    description = profile.address,
//                                    data = mapOf(
//                                        "name" to profile.name,
//                                        "address" to profile.address,
//                                        "phone" to profile.phone,
//                                        "email" to profile.email
//                                    )
//                                )
//                            )
//                        } else emptyList()
//                    }
//                    is SettingsModule.GeneralSettings -> {
//                        val settings = settingsRepository.getGeneralSettings()
//                        if (settings != null) {
//                            listOf(
//                                SettingsItem(
//                                    id = "1",
//                                    name = "General Settings",
//                                    description = "Currency: ${settings.currency}",
//                                    data = mapOf(
//                                        "currency" to settings.currency,
//                                        "language" to settings.language,
//                                        "timezone" to settings.timezone
//                                    )
//                                )
//                            )
//                        } else emptyList()
//                    }
//                    is SettingsModule.CreateVoucher -> {
//                        val vouchers = settingsRepository.getAllVouchers()
//                        vouchers.map { voucher ->
//                            SettingsItem(
//                                id = voucher.id.toString(),
//                                name = voucher.code,
//                                description = "Discount: ${voucher.discount}%",
//                                data = mapOf(
//                                    "code" to voucher.code,
//                                    "discount" to voucher.discount.toString(),
//                                    "expiry_date" to voucher.expiryDate,
//                                    "is_active" to voucher.isActive.toString()
//                                )
//                            )
//                        }
//                    }
//                    is SettingsModule.Counter -> {
//                        val counters = settingsRepository.getAllCounters()
//                        counters.map { counter ->
//                            SettingsItem(
//                                id = counter.id.toString(),
//                                name = counter.name,
//                                description = "Status: ${if (counter.isActive) "Active" else "Inactive"}",
//                                data = mapOf(
//                                    "name" to counter.name,
//                                    "is_active" to counter.isActive.toString()
//                                )
//                            )
//                        }
//                    }
                    SettingsModule.Counter -> TODO()
                    SettingsModule.CreateVoucher -> TODO()
                    SettingsModule.Customer -> TODO()
                    SettingsModule.GeneralSettings -> TODO()
                    SettingsModule.Menu -> TODO()
                    SettingsModule.MenuCategory -> TODO()
                    SettingsModule.MenuItem -> TODO()
                    SettingsModule.Printer -> TODO()
                    SettingsModule.RestaurantProfile -> TODO()
                    SettingsModule.Role -> TODO()
                    SettingsModule.Staff -> TODO()
                    SettingsModule.Tax -> TODO()
                    SettingsModule.TaxSplit -> TODO()
                }
                _uiState.value = SettingsUiState.Success(items)
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("Failed to load ${module.title}: ${e.message}")
            }
        }
    }

    fun addItem(module: SettingsModule, data: Map<String, String>) {
        viewModelScope.launch {
            try {
                when (module) {
                    is SettingsModule.Area -> {
                        val area = Area(
                            area_id = 0,
                            area_name = data["name"] ?: "",
                            isActvice = data["status"]?.toBoolean() ?: true,
                        )
                        settingsRepository.insertArea(area)
                    }
                    is SettingsModule.Table -> {
                        val table = Table(
                            table_id = 0,
                            table_name = data["name"] ?: "",
                            area_id = data["area_id"]?.toLongOrNull() ?: 0,
                            area_name = data["name"] ?: "",
                            seating_capacity = data["capacity"]?.toIntOrNull() ?: 0,
                            is_ac = (data["is_ac"]?.toBoolean() ?: false).toString(),
                            table_status = data["status"] ?: "",
                            table_availability = data["table_availability"] ?: ""
                        )
                        settingsRepository.insertTable(table)
                    }
//                    is SettingsModule.Menu -> {
//                        val menu = Menu(
//                            id = 0,
//                            name = data["name"] ?: "",
//                            description = data["description"] ?: "",
//                            isActive = data["is_active"]?.toBoolean() ?: true
//                        )
//                        settingsRepository.insertMenu(menu)
//                    }
//                    is SettingsModule.MenuCategory -> {
//                        val category = MenuCategory(
//                            id = 0,
//                            name = data["name"] ?: "",
//                            description = data["description"] ?: "",
//                            sortOrder = data["sort_order"]?.toIntOrNull() ?: 0
//                        )
//                        settingsRepository.insertMenuCategory(category)
//                    }
//                    is SettingsModule.MenuItem -> {
//                        val menuItem = MenuItem(
//                            menu_item_id = 0,
//                            menu_item_name = data["name"] ?: "",
//                            menu_item_name_tamil = "",
//                            rate = data["rate"]?.toDoubleOrNull() ?: 0.0,
//                            ac_rate = data["rate"]?.toDoubleOrNull() ?: 0.0,
//                            parcel_rate = data["rate"]?.toDoubleOrNull() ?: 0.0,
//                            item_cat_name = data["category"] ?: "",
//                            is_available = data["is_available"] ?: "YES"
//                        )
//                        settingsRepository.insertMenuItem(menuItem)
//                    }
//                    is SettingsModule.Customer -> {
//                        val customer = Customer(
//                            id = 0,
//                            name = data["name"] ?: "",
//                            phone = data["phone"] ?: "",
//                            email = data["email"] ?: "",
//                            address = data["address"] ?: ""
//                        )
//                        settingsRepository.insertCustomer(customer)
//                    }
//                    is SettingsModule.Staff -> {
//                        val staff = Staff(
//                            id = 0,
//                            name = data["name"] ?: "",
//                            role = data["role"] ?: "",
//                            phone = data["phone"] ?: "",
//                            email = data["email"] ?: ""
//                        )
//                        settingsRepository.insertStaff(staff)
//                    }
//                    is SettingsModule.Role -> {
//                        val role = Role(
//                            id = 0,
//                            name = data["name"] ?: "",
//                            description = data["description"] ?: "",
//                            permissions = data["permissions"] ?: ""
//                        )
//                        settingsRepository.insertRole(role)
//                    }
//                    is SettingsModule.Printer -> {
//                        val printer = Printer(
//                            id = 0,
//                            name = data["name"] ?: "",
//                            ipAddress = data["ip_address"] ?: "",
//                            port = data["port"]?.toIntOrNull() ?: 9100,
//                            type = data["type"] ?: "thermal"
//                        )
//                        settingsRepository.insertPrinter(printer)
//                    }
//                    is SettingsModule.Tax -> {
//                        val tax = Tax(
//                            id = 0,
//                            name = data["name"] ?: "",
//                            rate = data["rate"]?.toDoubleOrNull() ?: 0.0,
//                            type = data["type"] ?: "percentage",
//                            isActive = data["is_active"]?.toBoolean() ?: true
//                        )
//                        settingsRepository.insertTax(tax)
//                    }
//                    is SettingsModule.TaxSplit -> {
//                        val taxSplit = TaxSplit(
//                            id = 0,
//                            name = data["name"] ?: "",
//                            description = data["description"] ?: "",
//                            splitType = data["split_type"] ?: "percentage",
//                            percentage = data["percentage"]?.toDoubleOrNull() ?: 0.0
//                        )
//                        settingsRepository.insertTaxSplit(taxSplit)
//                    }
//                    is SettingsModule.CreateVoucher -> {
//                        val voucher = Voucher(
//                            id = 0,
//                            code = data["code"] ?: "",
//                            discount = data["discount"]?.toDoubleOrNull() ?: 0.0,
//                            expiryDate = data["expiry_date"] ?: "",
//                            isActive = data["is_active"]?.toBoolean() ?: true
//                        )
//                        settingsRepository.insertVoucher(voucher)
//                    }
//                    is SettingsModule.Counter -> {
//                        val counter = Counter(
//                            id = 0,
//                            name = data["name"] ?: "",
//                            isActive = data["is_active"]?.toBoolean() ?: true
//                        )
//                        settingsRepository.insertCounter(counter)
//                    }
//                    is SettingsModule.RestaurantProfile -> {
//                        val profile = RestaurantProfile(
//                            name = data["name"] ?: "",
//                            address = data["address"] ?: "",
//                            phone = data["phone"] ?: "",
//                            email = data["email"] ?: ""
//                        )
//                        settingsRepository.updateRestaurantProfile(profile)
//                    }
//                    is SettingsModule.GeneralSettings -> {
//                        val settings = GeneralSettings(
//                            currency = data["currency"] ?: "INR",
//                            language = data["language"] ?: "English",
//                            timezone = data["timezone"] ?: "Asia/Kolkata"
//                        )
//                        settingsRepository.updateGeneralSettings(settings)
//                    }
                    SettingsModule.Counter -> TODO()
                    SettingsModule.CreateVoucher -> TODO()
                    SettingsModule.Customer -> TODO()
                    SettingsModule.GeneralSettings -> TODO()
                    SettingsModule.Menu -> TODO()
                    SettingsModule.MenuCategory -> TODO()
                    SettingsModule.MenuItem -> TODO()
                    SettingsModule.Printer -> TODO()
                    SettingsModule.RestaurantProfile -> TODO()
                    SettingsModule.Role -> TODO()
                    SettingsModule.Staff -> TODO()
                    SettingsModule.Tax -> TODO()
                    SettingsModule.TaxSplit -> TODO()
                }
                loadModuleData(module) // Refresh data
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("Failed to add ${module.title}: ${e.message}")
            }
        }
    }

    fun editItem(item: SettingsItem) {
        viewModelScope.launch {
            try {
                // Implementation will depend on the specific module
                // This is a placeholder - you'd need to implement edit functionality
                // based on the current selected module
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("Failed to edit item: ${e.message}")
            }
        }
    }

    fun deleteItem(item: SettingsItem) {
        viewModelScope.launch {
            try {
                val itemId = item.id.toLongOrNull() ?: return@launch
                val currentModule = _selectedModule.value ?: return@launch

                when (currentModule) {
                    is SettingsModule.Area -> settingsRepository.deleteArea(itemId)
                    is SettingsModule.Table -> settingsRepository.deleteTable(itemId)
//                    is SettingsModule.Menu -> settingsRepository.deleteMenu(itemId)
//                    is SettingsModule.MenuCategory -> settingsRepository.deleteMenuCategory(itemId)
//                    is SettingsModule.MenuItem -> settingsRepository.deleteMenuItem(itemId)
//                    is SettingsModule.Customer -> settingsRepository.deleteCustomer(itemId)
//                    is SettingsModule.Staff -> settingsRepository.deleteStaff(itemId)
//                    is SettingsModule.Role -> settingsRepository.deleteRole(itemId)
//                    is SettingsModule.Printer -> settingsRepository.deletePrinter(itemId)
//                    is SettingsModule.Tax -> settingsRepository.deleteTax(itemId)
//                    is SettingsModule.TaxSplit -> settingsRepository.deleteTaxSplit(itemId)
//                    is SettingsModule.CreateVoucher -> settingsRepository.deleteVoucher(itemId)
//                    is SettingsModule.Counter -> settingsRepository.deleteCounter(itemId)
                    else -> {} // RestaurantProfile and GeneralSettings don't support delete
                }
                loadModuleData(currentModule) // Refresh data
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("Failed to delete item: ${e.message}")
            }
        }
    }

    fun setSelectedModule(module: SettingsModule) {
        _selectedModule.value = module
    }
}
