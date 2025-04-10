package me.mucloud.application.MK.ServerLauncher.internal.manage

import java.io.File

object Configuration {

    private var ConfigurationFolder: File = File("./conf")
    private var ConfigurationFile: File = File(ConfigurationFolder, "common.conf")
    private var PluginPackFolder: File = File("./plugins")
    private var LogFolder: File = File("./logs")

    private var ConfigurationVersion: Int = 0

}