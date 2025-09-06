package com.warriortech.resb.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.warriortech.resb.data.local.dao.*
import com.warriortech.resb.data.local.entity.*


@Database(
    entities = [
        TblAddOn::class,
        TblArea::class,
        TblBeforeOrderDetailsModify::class,
        TblBillModify::class,
        TblBilling::class,
        TblCompany::class,
        TblCounter::class,
        TblCustomer::class,
        TblGeneralSettings::class,
        TblItemAddOn::class,
        TblItemCategory::class,
        TblKitchenCategory::class,
        TblMenu::class,
        TblMenuItem::class,
        TblOrderDetails::class,
        TblOrderMaster::class,
        TblPrinter::class,
        TblRole::class,
        TblSaleAddOn::class,
        TblStaff::class,
        TblTable::class,
        TblTax::class,
        TblTaxSplit::class,
        TblUnit::class,
        TblVoucher::class,
        TblVoucherType::class,
    ],
    version = 3,
    exportSchema = true
)
abstract class RestaurantDatabase : RoomDatabase() {

    abstract fun tableDao(): TableDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun tblAddOnDao(): TblAddOnDao
    abstract fun tblAreaDao(): TblAreaDao
    abstract fun tblBeforeOrderDetailsModifyDao(): TblBeforeOrderDetailsModifyDao
    abstract fun tblBillModifyDao(): TblBillModifyDao
    abstract fun tblBillingDao(): TblBillingDao
    abstract fun tblCompanyDao(): TblCompanyDao
    abstract fun tblCounterDao(): TblCounterDao
    abstract fun tblCustomerDao(): TblCustomerDao
    abstract fun tblGeneralSettingsDao(): TblGeneralSettingsDao
    abstract fun tblItemAddOnDao(): TblItemAddOnDao
    abstract fun tblItemCategoryDao(): TblItemCategoryDao
    abstract fun tblKitchenCategoryDao(): TblKitchenCategoryDao
    abstract fun tblMenuDao(): TblMenuDao
    abstract fun tblOrderDetailsDao(): TblOrderDetailsDao
    abstract fun tblOrderMasterDao(): TblOrderMasterDao
    abstract fun tblPrinterDao(): TblPrinterDao
    abstract fun tblRoleDao(): TblRoleDao
    abstract fun tblSaleAddOnDao(): TblSaleAddOnDao
    abstract fun tblStaffDao(): TblStaffDao
    abstract fun tblTaxDao(): TblTaxDao
    abstract fun tblTaxSplitDao(): TblTaxSplitDao
    abstract fun tblUnitDao(): TblUnitDao
    abstract fun tblVoucherDao(): TblVoucherDao
    abstract fun tblVoucherTypeDao(): TblVoucherTypeDao
    abstract fun tblItemCategoryRelationDao(): TblItemCategoryRelationDao
    abstract fun tblCustomerRelationDao(): TblCustomerRelationDao
    abstract fun tblOrderMasterRelationDao(): TblOrderMasterRelationDao
    abstract fun tblStaffRelationDao(): TblStaffRelationDao
    abstract fun tblVoucherRelationDao(): TblVoucherRelationDao
    abstract fun tblKitchenCategoryRelationDao(): TblKitchenCategoryRelationDao
    abstract fun tblMenuRelationDao(): TblMenuRelationDao
    abstract fun tblTaxRelationDao(): TblTaxRelationDao
    abstract fun tblUnitRelationDao(): TblUnitRelationDao
    abstract fun tblMenuItemRelationDao(): TblMenuItemRelationDao
    abstract fun tblTableRelationDao(): TblTableRelationDao
    abstract fun tblAddOnRelationDao(): TblAddOnRelationDao
    abstract fun tblAreaRelationDao(): TblAreaRelationDao
    abstract fun tblCounterRelationDao(): TblCounterRelationDao
    abstract fun tblRoleRelationDao(): TblRoleRelationDao

    //    abstract fun orderDao(): OrderDao
//    abstract fun orderItemDao(): OrderItemDao
    
    companion object {
        @Volatile
        private var INSTANCE: RestaurantDatabase? = null
        
        fun getDatabase(context: Context): RestaurantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RestaurantDatabase::class.java,
                    "KTS_RESB"
                )
                .addMigrations(MIGRATION_2_3)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}