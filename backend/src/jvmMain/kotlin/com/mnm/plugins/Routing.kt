package com.mnm.plugins

import com.mnm.common.models.HelloWorld
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/hola") {
            call.respondText(HelloWorld().hi())
        }
    }
}
