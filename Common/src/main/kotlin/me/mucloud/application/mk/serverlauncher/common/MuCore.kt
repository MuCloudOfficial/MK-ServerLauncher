package me.mucloud.application.mk.serverlauncher.common

import me.mucloud.application.mk.serverlauncher.common.env.EnvPool
import me.mucloud.application.mk.serverlauncher.common.external.AppInfoStatus
import me.mucloud.application.mk.serverlauncher.common.external.SystemMonitor
import me.mucloud.application.mk.serverlauncher.common.manage.MuConfiguration
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool

interface MuCore{
    fun start()

    fun stop()

    fun getMuCoreInfo(): AppInfoStatus

    fun getMuCoreConfig(): MuConfiguration

    fun getSystemMonitor(): SystemMonitor

    fun getEnvPool(): EnvPool

    fun getServerPool(): ServerPool
}