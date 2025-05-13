package me.mucloud.application.MK.ServerLauncher.internal.manage

import java.io.File

object Configuration {

    private var ServerFolder: String = ".${File.separator}server"
    private var ConfigurationFolder: String = ".${File.separator}config"
    private var ConfigurationFile: String = ".${File.separator}common.conf"
    private var PluginPackFolder: String = ".${File.separator}plugins"
    private var LogFolder: String = ".${File.separator}log"

    private var ConfigurationVersion: Int = 0

    init{

    }

    fun getConfigurationFolder() = File(ConfigurationFolder)
    fun getServerFolder() = File(ServerFolder)

}