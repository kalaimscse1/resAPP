package com.warriortech.resb.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.warriortech.resb.data.local.dao.MenuItemDao
import com.warriortech.resb.data.local.dao.ModifierDao
import com.warriortech.resb.data.local.dao.OrderItemModifierDao
import com.warriortech.resb.data.local.dao.TableDao
import com.warriortech.resb.data.local.entity.MenuItemEntity
import com.warriortech.resb.data.local.entity.ModifierEntity
import com.warriortech.resb.data.local.entity.OrderItemModifierEntity
import com.warriortech.resb.data.local.entity.TableEntity


@Database(
    entities = [
        TableEntity::class,
        MenuItemEntity::class,
        ModifierEntity::class,
        OrderItemModifierEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class RestaurantDatabase : RoomDatabase() {

    abstract fun tableDao(): TableDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun modifierDao(): ModifierDao
    abstract fun orderItemModifierDao(): OrderItemModifierDao

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
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}