package me.mucloud.application.MK.ServerLauncher.view

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.FrameType
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import me.mucloud.application.MK.ServerLauncher.external.monitor.SystemMonitor
import me.mucloud.application.MK.ServerLauncher.internal.env.JavaEnvironment
import me.mucloud.application.MK.ServerLauncher.internal.env.MuEnvironment
import me.mucloud.application.MK.ServerLauncher.internal.server.ServerPool
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.AbstractServer
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.MCJEServer
import java.util.Collections
import kotlin.time.Duration.Companion.seconds

fun Application.initWebSocket() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(Json{ // Define the polymorphic Class and Subclass.
            serializersModule = SerializersModule {
                polymorphic(AbstractServer::class) {
                    subclass(MCJEServer::class, MCJEServer.serializer())
                }
                polymorphic(MuEnvironment::class){
                    subclass(JavaEnvironment::class, JavaEnvironment.serializer())
                }
            }
            prettyPrint = true
        })
    }
    routing {
        webSocket("/overview") {
            log.info("Connection [System Monitor] -> [Status: CONNECTED]")

            SystemMonitor.getStatus().collect { s ->
                sendSerialized(s)
            }
        }

        webSocket("/server/{server}"){
            val server = call.parameters["server"]
            if(server == null || ServerPool.getServer(server) == null) {
                return@webSocket call.respond(HttpStatusCode.BadRequest)
            }else{

            }
        }

        webSocket("/server/{server}/console") {
            val server = call.parameters["server"]
            if(server == null || ServerPool.getServer(server) == null) {
                return@webSocket call.respond(HttpStatusCode.BadRequest)
            }else{

            }
        }
    }
}