package me.mucloud.application.MK.ServerLauncher

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import me.mucloud.application.MK.ServerLauncher.external.monitor.SystemMonitor
import me.mucloud.application.MK.ServerLauncher.view.initRoute
import me.mucloud.application.MK.ServerLauncher.view.initWebSocket

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {

    SystemMonitor.initMonitor(1)

    initWebSocket()
    initRoute()
    routing {
        singlePageApplication {
            useResources = true
            filesPath = "vue-output"
            defaultPage = "index.html"
            ignoreFiles {
                it.endsWith(".txt")
            }
        }
    }
}
