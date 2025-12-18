package com.warriortech.resb.di

import com.warriortech.resb.network.ApiClient
import com.warriortech.resb.repository.AuthRepository
import com.warriortech.resb.repository.MenuRepository
import com.warriortech.resb.repository.OrderRepository
import com.warriortech.resb.repository.TableRepository
import org.koin.core.module.Module
import org.koin.dsl.module

fun commonModule(baseUrl: String, tokenProvider: () -> String?): Module = module {
    single { ApiClient(baseUrl, tokenProvider) }
    
    single { AuthRepository(get()) }
    single { MenuRepository(get()) }
    single { OrderRepository(get()) }
    single { TableRepository(get()) }
}
