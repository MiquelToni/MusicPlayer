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
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val jsonSerializer = Json {
    serializersModule = SerializersModule {
        polymorphic(Command::class) {
            subclass(PlayCommand::class)
            subclass(StopCommand::class)
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

    fun subscribeToPlayerState(scope: CoroutineScope): MutableStateFlow<PlayerState?> {
        val flow = MutableStateFlow<PlayerState?>(null)
        scope.launch {
            webSocket(Routes.api.playlist) {
                flow.value = receiveDeserialized<PlayerState>()
            }
        }
        return flow
    }

    suspend fun post(
        endpoint: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ) = client.post(urlString = getEndpoint(endpoint, schema = HTTP), block = block)

    suspend fun uploadFile(
        fileName: String,
        fileContents: ByteArray,
        block: HttpRequestBuilder.() -> Unit = {}
    ) {
        client.submitFormWithBinaryData(
            url = getEndpoint(Routes.api.postNewSong, schema=HTTP),
            formData = formData {
                append("file", fileContents, headers = Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=$fileName")
                })
            },
            block = block
        )
    }
}

private fun portAsString() = if (PORT != null) ":$PORT" else ""