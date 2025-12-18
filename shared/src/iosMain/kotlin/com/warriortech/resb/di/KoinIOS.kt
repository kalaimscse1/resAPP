package com.warriortech.resb.di

import com.warriortech.resb.database.DatabaseDriverFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoinIOS(
    baseUrl: String,
    tokenProvider: () -> String?
) {
    startKoin {
        modules(
            sharedModule(baseUrl, tokenProvider),
            iosModule()
        )
    }
}

fun iosModule() = module {
    single { DatabaseDriverFactory() }
}

object IOSHelper {
    fun doInitKoin(baseUrl: String) {
        initKoinIOS(baseUrl) { null }
    }
}
