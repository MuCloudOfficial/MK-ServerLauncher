package me.mucloud.application.MK.ServerLauncher

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.http.content.ignoreFiles
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.netty.EngineMain
import io.ktor.server.routing.routing
import me.mucloud.application.MK.ServerLauncher.external.monitor.SystemMonitor
import me.mucloud.application.MK.ServerLauncher.internal.env.EnvPool
import me.mucloud.application.MK.ServerLauncher.internal.manage.Configuration
import me.mucloud.application.MK.ServerLauncher.internal.protocol.packets.MuBroadcastPacket
import me.mucloud.application.MK.ServerLauncher.internal.protocol.packets.MuPacket
import me.mucloud.application.MK.ServerLauncher.internal.protocol.packets.MuSend2ServerPacket
import me.mucloud.application.MK.ServerLauncher.internal.protocol.packets.MuServerConfigPacket
import me.mucloud.application.MK.ServerLauncher.internal.protocol.packets.MuServerInfoPacket
import me.mucloud.application.MK.ServerLauncher.internal.protocol.packets.MuServerStartPacket
import me.mucloud.application.MK.ServerLauncher.internal.protocol.packets.MuServerStopPacket
import me.mucloud.application.MK.ServerLauncher.internal.server.ServerPool
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.MCJEServer
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.MCJEServerAdapter
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.ServerType
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.ServerTypeSerializer
import me.mucloud.application.MK.ServerLauncher.view.initRoute
import me.mucloud.application.MK.ServerLauncher.view.initWebSocket
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    monitor.subscribe(ApplicationStarted){
        SystemMonitor.initMonitor(1)
        Configuration.init()
        EnvPool.scanEnv()
        ServerPool.scanServer()
    }

    monitor.subscribe(ApplicationStopped){
        SystemMonitor.close()
        EnvPool.save()
        ServerPool.saveServers()
    }

    initRoute()
    initWebSocket()
    routing {
        singlePageApplication {
            useResources = false
            filesPath = "MuView"
            defaultPage = "index.html"
            ignoreFiles {
                it.endsWith(".txt")
            }
        }
    }
}

val log: Logger = LoggerFactory.getLogger("MK-ServerLauncher | MuCore")

fun initGson(): GsonBuilder{
    val factory =
        RuntimeTypeAdapterFactory
            .of(MuPacket::class.java, "type")
            .registerSubtype(MuServerConfigPacket::class.java, "config")
            .registerSubtype(MuSend2ServerPacket::class.java, "msg.in:send")
            .registerSubtype(MuBroadcastPacket::class.java, "msg.in:broadcast")
            .registerSubtype(MuServerInfoPacket::class.java, "server:info")
            .registerSubtype(MuServerStartPacket::class.java, "server:start")
            .registerSubtype(MuServerStopPacket::class.java, "server:stop")

    return GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(MCJEServer::class.java, MCJEServerAdapter)
        .registerTypeAdapter(ServerType::class.java, ServerTypeSerializer)
        .registerTypeAdapterFactory(factory)
}

fun getGson(): Gson = initGson().create()
