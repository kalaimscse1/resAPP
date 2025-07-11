
package com.warriortech.resb.data.api

import com.warriortech.resb.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Area endpoints
    @GET("areas")
    suspend fun getAllAreas(): Response<List<Area>>
    
    @POST("areas")
    suspend fun createArea(@Body area: Area): Response<Area>
    
    @PUT("areas/{id}")
    suspend fun updateArea(@Path("id") id: Int, @Body area: Area): Response<Area>
    
    @DELETE("areas/{id}")
    suspend fun deleteArea(@Path("id") id: Int): Response<Unit>

    // Table endpoints
    @GET("tables")
    suspend fun getAllTables(): Response<List<Table>>
    
    @POST("tables")
    suspend fun createTable(@Body table: Table): Response<Table>
    
    @PUT("tables/{id}")
    suspend fun updateTable(@Path("id") id: Int, @Body table: Table): Response<Table>
    
    @DELETE("tables/{id}")
    suspend fun deleteTable(@Path("id") id: Int): Response<Unit>

    // Menu endpoints
    @GET("menus")
    suspend fun getAllMenus(): Response<List<Menu>>
    
    @POST("menus")
    suspend fun createMenu(@Body menu: Menu): Response<Menu>
    
    @PUT("menus/{id}")
    suspend fun updateMenu(@Path("id") id: Int, @Body menu: Menu): Response<Menu>
    
    @DELETE("menus/{id}")
    suspend fun deleteMenu(@Path("id") id: Int): Response<Unit>

    // Menu Category endpoints
    @GET("menu-categories")
    suspend fun getAllMenuCategories(): Response<List<MenuCategory>>
    
    @POST("menu-categories")
    suspend fun createMenuCategory(@Body category: MenuCategory): Response<MenuCategory>
    
    @PUT("menu-categories/{id}")
    suspend fun updateMenuCategory(@Path("id") id: Int, @Body category: MenuCategory): Response<MenuCategory>
    
    @DELETE("menu-categories/{id}")
    suspend fun deleteMenuCategory(@Path("id") id: Int): Response<Unit>

    // Menu Item endpoints
    @GET("menu-items")
    suspend fun getAllMenuItems(): Response<List<MenuItem>>
    
    @POST("menu-items")
    suspend fun createMenuItem(@Body menuItem: MenuItem): Response<MenuItem>
    
    @PUT("menu-items/{id}")
    suspend fun updateMenuItem(@Path("id") id: Int, @Body menuItem: MenuItem): Response<MenuItem>
    
    @DELETE("menu-items/{id}")
    suspend fun deleteMenuItem(@Path("id") id: Int): Response<Unit>

    // Customer endpoints
    @GET("customers")
    suspend fun getAllCustomers(): Response<List<Customer>>
    
    @POST("customers")
    suspend fun createCustomer(@Body customer: Customer): Response<Customer>
    
    @PUT("customers/{id}")
    suspend fun updateCustomer(@Path("id") id: Int, @Body customer: Customer): Response<Customer>
    
    @DELETE("customers/{id}")
    suspend fun deleteCustomer(@Path("id") id: Int): Response<Unit>

    // Staff endpoints
    @GET("staff")
    suspend fun getAllStaff(): Response<List<Staff>>
    
    @POST("staff")
    suspend fun createStaff(@Body staff: Staff): Response<Staff>
    
    @PUT("staff/{id}")
    suspend fun updateStaff(@Path("id") id: Int, @Body staff: Staff): Response<Staff>
    
    @DELETE("staff/{id}")
    suspend fun deleteStaff(@Path("id") id: Int): Response<Unit>
}
