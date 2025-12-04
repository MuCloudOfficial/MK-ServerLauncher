package me.mucloud.application.mk.serverlauncher.hpe

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import me.mucloud.application.mk.serverlauncher.common.MuCoreMini
import me.mucloud.application.mk.serverlauncher.common.env.JavaEnvironment
import me.mucloud.application.mk.serverlauncher.common.env.JavaEnvironmentAdapter
import me.mucloud.application.mk.serverlauncher.common.server.MCJEServer
import me.mucloud.application.mk.serverlauncher.common.server.MCJEServerAdapter
import me.mucloud.application.mk.serverlauncher.common.server.MCJEServerType
import me.mucloud.application.mk.serverlauncher.common.server.ServerTypeSerializer
import me.mucloud.application.mk.serverlauncher.hpe.mulink.initSocket
import me.mucloud.application.mk.serverlauncher.hpe.mulink.initWebSocket
import me.mucloud.application.mk.serverlauncher.hpe.session.initMuSessionManager
import me.mucloud.application.mk.serverlauncher.hpe.view.initRoute

var MuCore: MuCoreMini = MuCoreMini
    private set

val gson: Gson = GsonBuilder()
    .setPrettyPrinting()
    .registerTypeAdapter(JavaEnvironment::class.java, JavaEnvironmentAdapter)
    .registerTypeAdapter(MCJEServer::class.java, MCJEServerAdapter)
    .registerTypeAdapter(MCJEServerType::class.java, ServerTypeSerializer)
    .create()

fun main() {
    MuCore.start()
    embeddedServer(Netty, port = MuCore.getMuCoreConfig().muCorePort, module = Application::module).start(wait = true)
}

fun Application.module() {
    initMuSessionManager()
    initRoute()
    initWebSocket()
    initSocket()
    initMuView()
}

fun Application.initMuView(){
    routing {
        singlePageApplication {
//            useResources = false
//            filesPath = "MuView"
//            defaultPage = "index.html"
//            ignoreFiles {
//                it.endsWith(".txt")
//            }
            vue("MuView")
        }
    }
}
