package com.mnm.plugins

import com.mnm.common.models.Routes
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.utils.io.streams.*
import java.io.File

const val musicPath = "backend/src/jvmMain/resources/"
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
            if(songName == null) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respondText("Missing path parameter :songName")
                return@get
            }

            val filesThatMatchSongName = File(musicPath)
                .walk()
                .filter { it.nameWithoutExtension == songName && it.extension == "mp3" }

            if (filesThatMatchSongName.count() == 0)
            {
                call.response.status(HttpStatusCode.NotFound)
                call.respondText("Song '$songName' not found")
                return@get
            }

            val file = filesThatMatchSongName.first()
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "$songName.mp3")
                    .toString()
            )
            call.respondFile(file)
        }
        post(Routes.api.postNewSong) {
            val statusCode = call.receiveMultipart()
                .readAllParts()
                .find {
                    it.name == "file"
                }
                ?.let {
                    if(it is PartData.FileItem) {
                        it.streamProvider().use { inputStream ->
                            val file = File("$musicPath${it.originalFileName}")
                            file.outputStream().buffered().use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        it.dispose()
                        playerStateFlow.value = playerStateFlow.value.copy(playlist = generatePlaylist())
                        HttpStatusCode.OK
                    }
                    else null
                }
                ?: HttpStatusCode.BadRequest

            call.response.status(statusCode)
        }
    }
}