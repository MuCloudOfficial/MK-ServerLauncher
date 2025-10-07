package me.mucloud.application.mk.serverlauncher.hpe.view

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.launch
import me.mucloud.application.mk.serverlauncher.common.external.SystemMonitor
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool
import kotlin.time.Duration.Companion.seconds

fun Application.initWebSocket() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = GsonWebsocketContentConverter(Gson())
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
        webSocket("/server/{server}"){
            val server = call.parameters["server"] ?: return@webSocket call.respond(HttpStatusCode.BadRequest, "Server Not Found.")
            val target = ServerPool.getServer(server) ?: return@webSocket call.respond(HttpStatusCode.BadRequest, "Server Not Found.")
            launch{
                target.getServerFlow().collect{
                    sendSerialized(it)
                }
            }
        }
    }
}