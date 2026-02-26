package me.mucloud.application.mk.serverlauncher.mucore

import com.electronwill.nightconfig.core.file.FileConfig
import java.io.File
import java.nio.charset.StandardCharsets

class MuConfiguration{

    private val configFile = File("config.yml")
    private val configBase: FileConfig = FileConfig.builder(configFile)
        .charset(StandardCharsets.UTF_8)
        .defaultResource("/config.yml")
        .autoreload().sync()
        .build()

    var version: Int = configBase.getIntOrElse("ConfigVersion", 0)
    var serverFolder: String = configBase.getOrElse("ServerFolderPath", "servers")
    var logFolder: String = configBase.getOrElse("LogFolderPath", "logs")
    var muCorePort: Int = configBase.getIntOrElse("MuCorePort", 20038)
    var muLinkPort: Int = configBase.getIntOrElse("MuLinkPort", 20039)
    var systemMonitorInterval: Int = configBase.getIntOrElse("SystemMonitorInterval", 3)

    init{
        configBase.load()
        if(!getServerFolder().exists()) getServerFolder().mkdirs()
        if(!getLogFolder().exists()) getLogFolder().mkdirs()
    }

    fun getServerFolder(): File = File(serverFolder).absoluteFile
    fun getLogFolder(): File = File(logFolder).absoluteFile

    fun save(){
        configBase.save()
    }
}