package me.mucloud.application.MK.ServerLauncher

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import me.mucloud.application.MK.ServerLauncher.external.monitor.SystemMonitor
import me.mucloud.application.MK.ServerLauncher.internal.env.EnvPool
import me.mucloud.application.MK.ServerLauncher.internal.manage.Configuration
import me.mucloud.application.MK.ServerLauncher.internal.server.ServerPool
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
            filesPath = "view/dist"
            defaultPage = "index.html"
            ignoreFiles {
                it.endsWith(".txt")
            }
        }
    }
}

val log: Logger = LoggerFactory.getLogger("MK-ServerLauncher | MuCore")
