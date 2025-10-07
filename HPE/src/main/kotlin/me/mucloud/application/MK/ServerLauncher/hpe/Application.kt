package me.mucloud.application.mk.serverlauncher.hpe

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.http.content.ignoreFiles
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.netty.EngineMain
import io.ktor.server.routing.routing
import me.mucloud.application.mk.serverlauncher.common.env.EnvPool
import me.mucloud.application.mk.serverlauncher.common.manage.ConfigurationFactory
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool
import me.mucloud.application.mk.serverlauncher.common.external.SystemMonitor
import me.mucloud.application.mk.serverlauncher.hpe.view.initRoute
import me.mucloud.application.mk.serverlauncher.hpe.view.initWebSocket

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    monitor.subscribe(ApplicationStarted){
        SystemMonitor.initMonitor(1)
        ConfigurationFactory.getConfiguration()
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
