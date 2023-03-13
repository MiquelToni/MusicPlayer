package com.mnm

import com.mnm.common.LOCAL_DEV_PORT
import com.mnm.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*

fun main() {
    embeddedServer(CIO, port = LOCAL_DEV_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureSockets()
    configureRouting()
}

