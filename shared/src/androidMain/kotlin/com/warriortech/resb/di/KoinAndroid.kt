package com.warriortech.resb.di

import android.content.Context
import com.warriortech.resb.database.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoinAndroid(
    context: Context,
    baseUrl: String,
    tokenProvider: () -> String?
) {
    startKoin {
        androidContext(context)
        modules(
            sharedModule(baseUrl, tokenProvider),
            androidModule(context)
        )
    }
}

fun androidModule(context: Context) = module {
    single { DatabaseDriverFactory(context) }
}
