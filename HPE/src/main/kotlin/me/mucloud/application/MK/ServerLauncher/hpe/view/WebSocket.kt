package me.mucloud.application.mk.serverlauncher.hpe.view

import com.google.gson.JsonParser
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.GsonWebsocketContentConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import me.mucloud.application.mk.serverlauncher.common.packets.MuPacketParser
import me.mucloud.application.mk.serverlauncher.common.packets.getGson
import me.mucloud.application.mk.serverlauncher.hpe.external.monitor.SystemMonitor
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool

fun Application.initWebSocket() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = GsonWebsocketContentConverter(getGson())
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
            launch{
                incoming.consumeEach{
                    if(it is Frame.Text){
                        val json = JsonParser.parseString(it.readText()).asJsonObject
                        MuPacketParser.parseFromJson(json)
                    }
                }
            }
        }
    }
}