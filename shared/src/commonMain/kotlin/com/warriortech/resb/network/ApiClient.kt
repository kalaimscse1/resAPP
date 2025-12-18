package com.warriortech.resb.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun createHttpClient(): HttpClient

class ApiClient(
    private val baseUrl: String,
    private val tokenProvider: () -> String?
) {
    private val httpClient: HttpClient = createHttpClient().config {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 30000
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            tokenProvider()?.let { token ->
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    suspend fun get(endpoint: String): HttpResponse {
        return httpClient.get("$baseUrl$endpoint")
    }

    suspend fun post(endpoint: String, body: Any? = null): HttpResponse {
        return httpClient.post("$baseUrl$endpoint") {
            body?.let { setBody(it) }
        }
    }

    suspend fun put(endpoint: String, body: Any? = null): HttpResponse {
        return httpClient.put("$baseUrl$endpoint") {
            body?.let { setBody(it) }
        }
    }

    suspend fun delete(endpoint: String): HttpResponse {
        return httpClient.delete("$baseUrl$endpoint")
    }

    fun close() {
        httpClient.close()
    }
}
