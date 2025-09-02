package me.mucloud.application.mk.serverlauncher.common.manage

import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import me.mucloud.application.mk.serverlauncher.common.manage.ConfigurationFactory.ConfigurationFile
import me.mucloud.application.mk.serverlauncher.common.manage.ConfigurationFactory.ConfigurationFolder
import me.mucloud.application.mk.serverlauncher.common.utils.log
import net.mamoe.yamlkt.Comment
import net.mamoe.yamlkt.Yaml

@Serializable
data class Configuration(
    @Comment("Define Configuration Version, DO NOT MODIFY UNTIL IN NECESSARY!")
    private val ConfigurationVersion: Int = 0
){

    @Comment("Define the MuPlugins Folder Position")
    private val PluginFolder: String = "plugins"

    @Comment("Define the Minecraft Server Folder in installation")
    private val ServerFolder: String = "server"

    @Comment("Define the MK-ServerLauncher Folder including Log Files Position")
    private val LogFolder: String = "log"

    @Comment("Define the MuWSServer Port to communicate with Minecraft Servers by WebSocket")
    private val WSPort: Int = 20038

    @Comment("Define WebSocket Token")
    private val WSToken: String = "iLoveMu"

    fun getServerFolder(): File = File(ServerFolder).absoluteFile
    fun getLogFolder(): File = File(LogFolder).absoluteFile
    fun getWSPort(): Int = WSPort

    init{
        getLogFolder()
        getServerFolder().mkdirs()
    }

    fun save(){
        File(ConfigurationFolder, ConfigurationFile).writeText(Yaml.encodeToString(this))
        log.info("MKSL Configuration File Saved.")
    }

}

object ConfigurationFactory{

    internal const val ConfigurationFolder: String = "config"
    internal const val ConfigurationFile: String = "common.yml"
    internal var BufferedConfiguration: Configuration? = null

    private var firstCreate: Boolean = false

    init {
        firstCreate =
            File(ConfigurationFolder).absoluteFile.mkdirs() ||
                    File(ConfigurationFolder, ConfigurationFile).absoluteFile.createNewFile()
        if(firstCreate) log.warn("MK-ServerLauncher Configuration Module may first Launch, MKSL Configuration File Created.")
    }

    fun getConfiguration(): Configuration{
        if(BufferedConfiguration == null){
            if(firstCreate){
                Configuration().save()
            }
            BufferedConfiguration = Yaml.decodeFromString<Configuration>(
                File(ConfigurationFolder, ConfigurationFile).readText()
            )
        }
        return BufferedConfiguration!!
    }

    fun getConfigurationFolder(): File = File(ConfigurationFolder).absoluteFile
    fun getConfigurationFile(): File = File(getConfigurationFolder(), ConfigurationFile)

}