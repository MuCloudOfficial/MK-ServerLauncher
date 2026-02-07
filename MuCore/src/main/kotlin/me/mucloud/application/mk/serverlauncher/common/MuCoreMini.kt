package me.mucloud.application.mk.serverlauncher.common

import me.mucloud.application.mk.serverlauncher.common.env.EnvPool
import me.mucloud.application.mk.serverlauncher.common.external.AppInfoStatus
import me.mucloud.application.mk.serverlauncher.common.external.SystemMonitor
import me.mucloud.application.mk.serverlauncher.common.manage.MuConfiguration
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool

object MuCoreMini {

    private val MuCoreInfo: AppInfoStatus = AppInfoStatus("MuCore DEV Mini", "TinyNova V1 DEV.1")
    private val MuCoreConfiguration: MuConfiguration = MuConfiguration()

    fun start() {
        EnvPool.scanEnv()
        ServerPool.scanServer()
        SystemMonitor.initMonitor()
    }

    fun stop() {
        EnvPool.save()
        ServerPool.saveServers()
        SystemMonitor.close()
        MuCoreConfiguration.save()
    }

    fun getMuCoreInfo(): AppInfoStatus = MuCoreInfo

    fun getMuCoreConfig(): MuConfiguration = MuCoreConfiguration

}