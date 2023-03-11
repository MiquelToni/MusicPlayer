package com.mnm.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/json/kotlinx-serialization") {
                call.respond(mapOf("hello" to "world"))
            }

        route("/api") {
            get("/song/{songName}") {
                val songName = call.parameters["songName"]
                // send song
            }
            post("/playlist") {
                // accept multipart
                // save BLOB as file
            }
        }
    }
}
