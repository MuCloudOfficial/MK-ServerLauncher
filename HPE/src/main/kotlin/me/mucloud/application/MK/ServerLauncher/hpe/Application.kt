package me.mucloud.application.mk.serverlauncher.hpe

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import me.mucloud.application.mk.serverlauncher.common.MuCore
import me.mucloud.application.mk.serverlauncher.common.MuCoreMini
import me.mucloud.application.mk.serverlauncher.hpe.view.initRoute
import me.mucloud.application.mk.serverlauncher.hpe.view.initWebSocket

var MuCore: MuCore = MuCoreMini()
    private set

fun main() {
    embeddedServer(Netty, port = MuCore.getMuCoreConfig().getMuCorePort(), module = Application::module).start(wait = true)
}

fun Application.module() {
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
