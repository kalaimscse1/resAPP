
package com.warriortech.resb.data.repository


import com.warriortech.resb.model.*
import com.warriortech.resb.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val apiService: ApiService
) {

    // Area management
    suspend fun getAllAreas(): List<Area> {
        return try {
            apiService.getAllAreas()
        } catch (e: Exception) {
            throw Exception("Failed to fetch areas: ${e.message}")
        }
    }

    suspend fun insertArea(area: Area): Long {
        return try {
            val response = apiService.createArea(area)
            response.body()?.area_id ?: 0L
        } catch (e: Exception) {
            throw Exception("Failed to create area: ${e.message}")
        }
    }

    suspend fun updateArea(area: Area) {
        try {
            apiService.updateArea(area.area_id, area)
        } catch (e: Exception) {
            throw Exception("Failed to update area: ${e.message}")
        }
    }

    suspend fun deleteArea(areaId: Long) {
        try {
            apiService.deleteArea(areaId)
        } catch (e: Exception) {
            throw Exception("Failed to delete area: ${e.message}")
        }
    }

    // Table management
    suspend fun getAllTables(): List<Table> {
        return try {
            apiService.getAllTables()
        } catch (e: Exception) {
            throw Exception("Failed to fetch tables: ${e.message}")
        }
    }

    suspend fun insertTable(table: Table): Long {
        return try {
            val response = apiService.createTable(table)
            response.body()?.table_id ?: 0L
        } catch (e: Exception) {
            throw Exception("Failed to create table: ${e.message}")
        }
    }

    suspend fun updateTable(table: Table) {
        try {
            apiService.updateTable(table.table_id, table)
        } catch (e: Exception) {
            throw Exception("Failed to update table: ${e.message}")
        }
    }

    suspend fun deleteTable(tableId: Long) {
        try {
            apiService.deleteTable(tableId)
        } catch (e: Exception) {
            throw Exception("Failed to delete table: ${e.message}")
        }
    }
//
//    // Menu management
//    suspend fun getAllMenus(): List<Menu> {
//        return try {
//            apiService.getAllMenus()
//        } catch (e: Exception) {
//            throw Exception("Failed to fetch menus: ${e.message}")
//        }
//    }
//
//    suspend fun insertMenu(menu: Menu): Long {
//        return try {
//            val response = apiService.createMenu(menu)
//            response.id ?: 0L
//        } catch (e: Exception) {
//            throw Exception("Failed to create menu: ${e.message}")
//        }
//    }
//
//    suspend fun updateMenu(menu: Menu) {
//        try {
//            apiService.updateMenu(menu.id, menu)
//        } catch (e: Exception) {
//            throw Exception("Failed to update menu: ${e.message}")
//        }
//    }
//
//    suspend fun deleteMenu(menuId: Long) {
//        try {
//            apiService.deleteMenu(menuId)
//        } catch (e: Exception) {
//            throw Exception("Failed to delete menu: ${e.message}")
//        }
//    }
//
//    // Menu Category management
//    suspend fun getAllMenuCategories(): List<MenuCategory> {
//        return try {
//            apiService.getAllMenuCategories()
//        } catch (e: Exception) {
//            throw Exception("Failed to fetch menu categories: ${e.message}")
//        }
//    }
//
//    suspend fun insertMenuCategory(category: MenuCategory): Long {
//        return try {
//            val response = apiService.createMenuCategory(category)
//            response.id ?: 0L
//        } catch (e: Exception) {
//            throw Exception("Failed to create menu category: ${e.message}")
//        }
//    }
//
//    suspend fun updateMenuCategory(category: MenuCategory) {
//        try {
//            apiService.updateMenuCategory(category.id, category)
//        } catch (e: Exception) {
//            throw Exception("Failed to update menu category: ${e.message}")
//        }
//    }
//
//    suspend fun deleteMenuCategory(categoryId: Long) {
//        try {
//            apiService.deleteMenuCategory(categoryId)
//        } catch (e: Exception) {
//            throw Exception("Failed to delete menu category: ${e.message}")
//        }
//    }
//
//    // Menu Item management
//    suspend fun getAllMenuItems(): List<MenuItem> {
//        return try {
//            apiService.getAllMenuItems()
//        } catch (e: Exception) {
//            throw Exception("Failed to fetch menu items: ${e.message}")
//        }
//    }
//
//    suspend fun insertMenuItem(item: MenuItem): Long {
//        return try {
//            val response = apiService.createMenuItem(item)
//            response.menu_item_id ?: 0L
//        } catch (e: Exception) {
//            throw Exception("Failed to create menu item: ${e.message}")
//        }
//    }
//
//    suspend fun updateMenuItem(item: MenuItem) {
//        try {
//            apiService.updateMenuItem(item.menu_item_id, item)
//        } catch (e: Exception) {
//            throw Exception("Failed to update menu item: ${e.message}")
//        }
//    }
//
//    suspend fun deleteMenuItem(itemId: Long) {
//        try {
//            apiService.deleteMenuItem(itemId)
//        } catch (e: Exception) {
//            throw Exception("Failed to delete menu item: ${e.message}")
//        }
//    }
//
//    // Customer management
//    suspend fun getAllCustomers(): List<Customer> {
//        return try {
//            apiService.getAllCustomers()
//        } catch (e: Exception) {
//            throw Exception("Failed to fetch customers: ${e.message}")
//        }
//    }
//
//    suspend fun insertCustomer(customer: Customer): Long {
//        return try {
//            val response = apiService.createCustomer(customer)
//            response.id ?: 0L
//        } catch (e: Exception) {
//            throw Exception("Failed to create customer: ${e.message}")
//        }
//    }
//
//    suspend fun updateCustomer(customer: Customer) {
//        try {
//            apiService.updateCustomer(customer.id, customer)
//        } catch (e: Exception) {
//            throw Exception("Failed to update customer: ${e.message}")
//        }
//    }
//
//    suspend fun deleteCustomer(customerId: Long) {
//        try {
//            apiService.deleteCustomer(customerId)
//        } catch (e: Exception) {
//            throw Exception("Failed to delete customer: ${e.message}")
//        }
//    }
//
//    // Staff management
//    suspend fun getAllStaff(): List<Staff> {
//        return try {
//            apiService.getAllStaff()
//        } catch (e: Exception) {
//            throw Exception("Failed to fetch staff: ${e.message}")
//        }
//    }
//
//    suspend fun insertStaff(staff: Staff): Long {
//        return try {
//            val response = apiService.createStaff(staff)
//            response.id ?: 0L
//        } catch (e: Exception) {
//            throw Exception("Failed to create staff: ${e.message}")
//        }
//    }
//
//    suspend fun updateStaff(staff: Staff) {
//        try {
//            apiService.updateStaff(staff.id, staff)
//        } catch (e: Exception) {
//            throw Exception("Failed to update staff: ${e.message}")
//        }
//    }
//
//    suspend fun deleteStaff(staffId: Long) {
//        try {
//            apiService.deleteStaff(staffId)
//        } catch (e: Exception) {
//            throw Exception("Failed to delete staff: ${e.message}")
//        }
//    }
//
//    // Role management
//    suspend fun getAllRoles(): List<Role> {
//        return try {
//            apiService.getAllRoles()
//        } catch (e: Exception) {
//            throw Exception("Failed to fetch roles: ${e.message}")
//        }
//    }
//
//    suspend fun insertRole(role: Role): Long {
//        return try {
//            val response = apiService.createRole(role)
//            response.id ?: 0L
//        } catch (e: Exception) {
//            throw Exception("Failed to create role: ${e.message}")
//        }
//    }
//
//    suspend fun updateRole(role: Role) {
//        try {
//            apiService.updateRole(role.id, role)
//        } catch (e: Exception) {
//            throw Exception("Failed to update role: ${e.message}")
//        }
//    }
//
//    suspend fun deleteRole(roleId: Long) {
//        try {
//            apiService.deleteRole(roleId)
//        } catch (e: Exception) {
//            throw Exception("Failed to delete role: ${e.message}")
//        }
//    }
//
//    // Printer management
//    suspend fun getAllPrinters(): List<Printer> {
//        return try {
//            apiService.getAllPrinters()
//        } catch (e: Exception) {
//            throw Exception("Failed to fetch printers: ${e.message}")
//        }
//    }
//
//    suspend fun insertPrinter(printer: Printer): Long {
//        return try {
//            val response = apiService.createPrinter(printer)
//            response.id ?: 0L
//        } catch (e: Exception) {
//            throw Exception("Failed to create printer: ${e.message}")
//        }
//    }
//
//    suspend fun updatePrinter(printer: Printer) {
//        try {
//            apiService.updatePrinter(printer.id, printer)
//        } catch (e: Exception) {
//            throw Exception("Failed to update printer: ${e.message}")
//        }
//    }
//
//    suspend fun deletePrinter(printerId: Long) {
//        try {
//            apiService.deletePrinter(printerId)
//        } catch (e: Exception) {
//            throw Exception("Failed to delete printer: ${e.message}")
//        }
//    }
//
//    // Tax management
//    suspend fun getAllTaxes(): List<Tax> {
//        return try {
//            apiService.getAllTaxes()
//        } catch (e: Exception) {
//            throw Exception("Failed to fetch taxes: ${e.message}")
//        }
//    }
//
//    suspend fun insertTax(tax: Tax): Long {
//        return try {
//            val response = apiService.createTax(tax)
//            response.id ?: 0L
//        } catch (e: Exception) {
//            throw Exception("Failed to create tax: ${e.message}")
//        }
//    }
//
//    suspend fun updateTax(tax: Tax) {
//        try {
//            apiService.updateTax(tax.id, tax)
//        } catch (e: Exception) {
//            throw Exception("Failed to update tax: ${e.message}")
//        }
//    }
//
//    suspend fun deleteTax(taxId: Long) {
//        try {
//            apiService.deleteTax(taxId)
//        } catch (e: Exception) {
//            throw Exception("Failed to delete tax: ${e.message}")
//        }
//    }
//
//    // Tax Split management
//    suspend fun getAllTaxSplits(): List<TaxSplit> {
//        return try {
//            apiService.getAllTaxSplits()
//        } catch (e: Exception) {
//            throw Exception("Failed to fetch tax splits: ${e.message}")
//        }
//    }
//
//    suspend fun insertTaxSplit(taxSplit: TaxSplit): Long {
//        return try {
//            val response = apiService.createTaxSplit(taxSplit)
//            response.id ?: 0L
//        } catch (e: Exception) {
//            throw Exception("Failed to create tax split: ${e.message}")
//        }
//    }
//
//    suspend fun updateTaxSplit(taxSplit: TaxSplit) {
//        try {
//            apiService.updateTaxSplit(taxSplit.id, taxSplit)
//        } catch (e: Exception) {
//            throw Exception("Failed to update tax split: ${e.message}")
//        }
//    }
//
//    suspend fun deleteTaxSplit(taxSplitId: Long) {
//        try {
//            apiService.deleteTaxSplit(taxSplitId)
//        } catch (e: Exception) {
//            throw Exception("Failed to delete tax split: ${e.message}")
//        }
//    }
//
//    // Restaurant Profile management
//    suspend fun getRestaurantProfile(): RestaurantProfile? {
//        return try {
//            apiService.getRestaurantProfile()
//        } catch (e: Exception) {
//            throw Exception("Failed to fetch restaurant profile: ${e.message}")
//        }
//    }
//
//    suspend fun updateRestaurantProfile(profile: RestaurantProfile) {
//        try {
//            apiService.updateRestaurantProfile(profile)
//        } catch (e: Exception) {
//            throw Exception("Failed to update restaurant profile: ${e.message}")
//        }
//    }
//
//    // General Settings management
//    suspend fun getGeneralSettings(): GeneralSettings? {
//        return try {
//            apiService.getGeneralSettings()
//        } catch (e: Exception) {
//            throw Exception("Failed to fetch general settings: ${e.message}")
//        }
//    }
//
//    suspend fun updateGeneralSettings(settings: GeneralSettings) {
//        try {
//            apiService.updateGeneralSettings(settings)
//        } catch (e: Exception) {
//            throw Exception("Failed to update general settings: ${e.message}")
//        }
//    }
//
//    // Voucher management
//    suspend fun getAllVouchers(): List<Voucher> {
//        return try {
//            apiService.getAllVouchers()
//        } catch (e: Exception) {
//            throw Exception("Failed to fetch vouchers: ${e.message}")
//        }
//    }
//
//    suspend fun insertVoucher(voucher: Voucher): Long {
//        return try {
//            val response = apiService.createVoucher(voucher)
//            response.id ?: 0L
//        } catch (e: Exception) {
//            throw Exception("Failed to create voucher: ${e.message}")
//        }
//    }
//
//    suspend fun updateVoucher(voucher: Voucher) {
//        try {
//            apiService.updateVoucher(voucher.id, voucher)
//        } catch (e: Exception) {
//            throw Exception("Failed to update voucher: ${e.message}")
//        }
//    }
//
//    suspend fun deleteVoucher(voucherId: Long) {
//        try {
//            apiService.deleteVoucher(voucherId)
//        } catch (e: Exception) {
//            throw Exception("Failed to delete voucher: ${e.message}")
//        }
//    }
//
//    // Counter management
//    suspend fun getAllCounters(): List<Counter> {
//        return try {
//            apiService.getAllCounters()
//        } catch (e: Exception) {
//            throw Exception("Failed to fetch counters: ${e.message}")
//        }
//    }
//
//    suspend fun insertCounter(counter: Counter): Long {
//        return try {
//            val response = apiService.createCounter(counter)
//            response.id ?: 0L
//        } catch (e: Exception) {
//            throw Exception("Failed to create counter: ${e.message}")
//        }
//    }
//
//    suspend fun updateCounter(counter: Counter) {
//        try {
//            apiService.updateCounter(counter.id, counter)
//        } catch (e: Exception) {
//            throw Exception("Failed to update counter: ${e.message}")
//        }
//    }
//
//    suspend fun deleteCounter(counterId: Long) {
//        try {
//            apiService.deleteCounter(counterId)
//        } catch (e: Exception) {
//            throw Exception("Failed to delete counter: ${e.message}")
//        }
//    }
}
