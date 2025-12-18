package com.warriortech.resb.di

import com.warriortech.resb.network.ApiClient
import com.warriortech.resb.repository.AuthRepository
import com.warriortech.resb.repository.MenuRepository
import com.warriortech.resb.repository.OrderRepository
import com.warriortech.resb.repository.TableRepository
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(
    baseUrl: String,
    tokenProvider: () -> String?,
    appDeclaration: KoinAppDeclaration = {}
) = startKoin {
    appDeclaration()
    modules(sharedModule(baseUrl, tokenProvider))
}

fun sharedModule(baseUrl: String, tokenProvider: () -> String?): Module = module {
    single { ApiClient(baseUrl, tokenProvider) }
    
    single { AuthRepository(get()) }
    single { MenuRepository(get()) }
    single { OrderRepository(get()) }
    single { TableRepository(get()) }
}

object SharedConfig {
    var baseUrl: String = ""
    var tokenProvider: () -> String? = { null }
    
    fun configure(baseUrl: String, tokenProvider: () -> String?) {
        this.baseUrl = baseUrl
        this.tokenProvider = tokenProvider
    }
}
