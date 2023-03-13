package com.mnm.common

internal const val LOCAL = true
const val LOCAL_DEV_PORT = 8080

val HTTP = if(LOCAL) "http" else "https"
val WS = if(LOCAL) "ws" else "wss"

internal val PORT = if(LOCAL) LOCAL_DEV_PORT else null
internal val HOSTNAME = if(LOCAL) "localhost" else "827d-85-58-29-37.eu.ngrok.io"