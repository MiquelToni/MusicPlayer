package com.mnm.common.networking

import com.mnm.common.models.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
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

val client = HttpClient {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(jsonSerializer)
    }
    install(ContentNegotiation) { json(jsonSerializer) }
}