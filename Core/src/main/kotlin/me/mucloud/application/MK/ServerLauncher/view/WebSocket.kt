package me.mucloud.application.MK.ServerLauncher.view

import com.google.gson.GsonBuilder
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import me.mucloud.application.MK.ServerLauncher.external.monitor.SystemMonitor
import me.mucloud.application.MK.ServerLauncher.internal.server.ServerPool
import kotlin.time.Duration.Companion.seconds

fun Application.initWebSocket() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = GsonWebsocketContentConverter(GsonBuilder().setPrettyPrinting().create())
    }
    routing {
        // WebSocket >> Fetch System Status & AppInfo Pack & Server Status Info Flow
        webSocket("/overview") {
            log.info("Connection [System Monitor] -> [Status: CONNECTED]") // TODO("Log Sys Rebase")

            SystemMonitor.getStatus().collect { s ->
                sendSerialized(s)
            }
        }

        // WebSocket >> Fetch Servers Info Flow
        webSocket("/server/{server}"){ // TODO("Log Sys Rebase")
            val server = call.parameters["server"]
            if(server == null || ServerPool.getServer(server) == null) {
                return@webSocket call.respond(HttpStatusCode.BadRequest)
            }else{

            }
        }

        webSocket("/server/{server}/console") { // TODO("Log Sys Rebase")
            val server = call.parameters["server"]
            if(server == null || ServerPool.getServer(server) == null) {
                return@webSocket call.respond(HttpStatusCode.BadRequest)
            }else{

            }
        }
    }
}