package com.mnm.plugins

import com.mnm.common.models.Routes
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/hola") {
            call.respondText("hola")
        }
        get(Routes.api.getSong) {
            val songName = call.parameters["songName"]
            // send song
        }
        post(Routes.api.postNewSong) {
            // accept multipart
            // save BLOB as file
        }
    }
}
