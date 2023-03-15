package com.mnm.plugins

import com.mnm.common.networking.jsonSerializer
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(jsonSerializer)
    }
}
