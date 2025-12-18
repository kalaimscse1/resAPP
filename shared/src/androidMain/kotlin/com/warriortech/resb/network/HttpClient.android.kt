package com.warriortech.resb.network

import io.ktor.client.*
import io.ktor.client.engine.android.*

actual fun createHttpClient(): HttpClient {
    return HttpClient(Android) {
        engine {
            connectTimeout = 15_000
            socketTimeout = 30_000
        }
    }
}
