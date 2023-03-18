package com.mnm.common.networking

import com.mnm.common.HOSTNAME
import com.mnm.common.HTTP
import com.mnm.common.PORT
import com.mnm.common.WS
import com.mnm.common.models.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val jsonSerializer = Json {
    serializersModule = SerializersModule {
        polymorphic(Command::class) {
            subclass(PrepareSong::class)
            subclass(SeekTo::class)
            subclass(Pause::class)
            subclass(Play::class)
            subclass(Stop::class)
        }
    }
}

object Http {
    fun getEndpoint(
        endpoint: String,
        schema: String = HTTP,
    ) = "$schema://$HOSTNAME${portAsString()}$endpoint"

    private val client = HttpClient {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(jsonSerializer)
        }
        install(ContentNegotiation) { json(jsonSerializer) }
    }

    suspend fun webSocket(
        endpoint: String,
        request: HttpRequestBuilder.() -> Unit = {},
        block: suspend DefaultClientWebSocketSession.() -> Unit
    ) {
        val url = getEndpoint(endpoint, schema = WS)
        client.webSocket(urlString = url, request = request, block = block)
    }
}

private fun portAsString() = if (PORT != null) ":$PORT" else ""