package me.mucloud.application.mk.serverlauncher.common

import me.mucloud.application.mk.serverlauncher.common.env.EnvPool
import me.mucloud.application.mk.serverlauncher.common.external.AppInfoStatus
import me.mucloud.application.mk.serverlauncher.common.external.SystemMonitor
import me.mucloud.application.mk.serverlauncher.common.manage.MuConfiguration
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool

class MuCoreMini: MuCore{

    private val MuCoreInfo: AppInfoStatus = AppInfoStatus("MuCore DEV Mini", "TinyNova V1 DEV.1")
    private val MuCoreConfiguration: MuConfiguration = MuConfiguration()

    private val EnvPool: EnvPool = EnvPool()
    private val ServerPool: ServerPool = ServerPool()
    private val SystemMonitor: SystemMonitor = SystemMonitor(this)

    override fun start(){
        MuCoreConfiguration
        EnvPool.scanEnv()
        ServerPool.scanServer()
        SystemMonitor.initMonitor()
    }

    override fun stop(){
        EnvPool.save()
        ServerPool.saveServers()
        SystemMonitor.close()
        MuCoreConfiguration.save()
    }

    override fun getMuCoreInfo(): AppInfoStatus = MuCoreInfo

    override fun getMuCoreConfig(): MuConfiguration = MuCoreConfiguration

    override fun getSystemMonitor(): SystemMonitor = SystemMonitor

    override fun getEnvPool(): EnvPool = EnvPool

    override fun getServerPool(): ServerPool = ServerPool

}