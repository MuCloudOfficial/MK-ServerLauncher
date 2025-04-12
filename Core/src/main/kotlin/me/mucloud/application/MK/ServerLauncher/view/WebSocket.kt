package me.mucloud.application.MK.ServerLauncher.view

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import me.mucloud.application.MK.ServerLauncher.internal.env.JavaEnvironment
import me.mucloud.application.MK.ServerLauncher.internal.env.MuEnvironment
import me.mucloud.application.MK.ServerLauncher.internal.server.ServerPool
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.AbstractServer
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.MCJEServer
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
        webSocket("/api/v1/overview") {
            log.info("Connection [Overview Flow] -> [Status: CONNECTED]")
            val msg = ServerPool.sendOverview()
            send(msg.toString())
        }

        webSocket("/api/v1/servers") {
            log.info("Connection [Servers Flow] -> [Status: CONNECTED]")
            val list = listOf<AbstractServer>(
                MCJEServer(mcs_name = "MuServer1", mcs_version = "1.19.2", mcs_type = "Paper", mcs_desc = "???", mcs_location = "Unknown", mcs_env = JavaEnvironment("Java","Unknown","Unknown")),
                MCJEServer(mcs_name = "MuServer2", mcs_version = "1.21.4", mcs_type = "Paper", mcs_desc = "???", mcs_location = "Unknown", mcs_env = JavaEnvironment("Java","Unknown","Unknown")),
            )
            sendSerialized(list)
        }
    }
}