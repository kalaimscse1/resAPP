package com.warriortech.resb.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
//import com.warriortech.resb.data.local.dao.MenuItemDao
//import com.warriortech.resb.data.local.dao.OrderDao
//import com.warriortech.resb.data.local.dao.OrderItemDao
import com.warriortech.resb.data.local.dao.TableDao
//import com.warriortech.resb.data.local.entity.MenuItemEntity
//import com.warriortech.resb.data.local.entity.OrderEntity
//import com.warriortech.resb.data.local.entity.OrderItemEntity
import com.warriortech.resb.data.local.entity.TableEntity

@Database(
    entities = [
        TableEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class RestaurantDatabase : RoomDatabase() {
    
    abstract fun tableDao(): TableDao
//    abstract fun menuItemDao(): MenuItemDao
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
                    "restaurant_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}