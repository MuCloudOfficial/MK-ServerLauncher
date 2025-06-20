package me.mucloud.application.mk.serverlauncher.common.manage

import java.io.File
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Configuration {

    private var ServerFolder: String = "server"
    private var ConfigurationFolder: String = "config"
    private var ConfigurationFile: String = "common.conf"
    private var PluginPackFolder: String = "plugins"
    private var LogFolder: String = "log"

    private var ConfigurationVersion: Int = 0

    fun getConfigurationFolder(): File = File(ConfigurationFolder).absoluteFile
    fun getServerFolder(): File = File(ServerFolder).absoluteFile
    fun getConfigFile(): File = File(getConfigurationFolder(), ConfigurationFile).absoluteFile
    fun getLogFolder(): File = File(LogFolder).absoluteFile

    fun init(){
        getLogFolder()
        getConfigurationFolder().mkdirs()
        getConfigFile().createNewFile()
        getServerFolder().mkdirs()
    }

}

val log: Logger = LoggerFactory.getLogger("MK-ServerLauncher | MuCore")