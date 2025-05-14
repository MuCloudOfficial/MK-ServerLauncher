package me.mucloud.application.MK.ServerLauncher.internal.server.mcserver

import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import me.mucloud.application.MK.ServerLauncher.internal.env.JavaEnvironment
import me.mucloud.application.MK.ServerLauncher.internal.manage.Configuration
import me.mucloud.application.MK.ServerLauncher.internal.server.ServerPool
import java.io.File
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

/**
 * MC Java Edition Server.
 *
 * MuPack Internal Server Template
 *
 * @since DEV.16
 * @author Mu_Cloud
 */
@Serializable
data class MCJEServer(
    private var name: String,
    private val version: String,
    private var type: String,
    private var desc: String,
    private var port: Int = 25565,
    @Contextual private var env: JavaEnvironment,
    @Contextual private var config: Config
){

    @Contextual private var location: File = File(Configuration.getServerFolder(), name)
    @Contextual private var running: Boolean = false
    private val totalFailCount: Int = 0
    private val totalPassCount: Int = 0
    @Transient private val consoleFlow: MutableSharedFlow<String> = MutableSharedFlow()

    init{
        type = ServerPool.getAvailableType()[type]!!
        if(!location.exists()){
            location.mkdirs()
        }
    }

    fun start() {
        running = true
    }

    fun stop() {
        running = false
    }

    fun getName(): String = name
    fun setName(name: String) { this.name = name }

    fun isRunning(): Boolean = running

    fun totalFailCount(): Int = totalFailCount

    fun totalPassCount(): Int = totalPassCount

    fun getDescription(): String = desc
    fun setDescription(desc: String){ this.desc = desc }

    fun getFolder(): File = location

    fun getPort(): Int = port
    fun setPort(port: Int) { this.port = port }

    fun getVersion(): String = version

    fun getType(): String = type

    fun getEnv(): JavaEnvironment = env
    fun setEnv(env: JavaEnvironment) { this.env = env }

    fun lastLaunchTime(): LocalDateTime = LocalDateTime.now() //todo

    fun getConfig(): Config = config

    fun saveToFile(){
        FileWriter(File(getFolder(), "MK-ServerLauncher.json").also { if(!it.exists()) it.createNewFile() }, StandardCharsets.UTF_8).also {
            it.write(GsonBuilder().setPrettyPrinting().create().toJson(this))
            it.flush()
        }
    }

    @Serializable
    data class Config(
        internal var isOnline: Boolean = true,
        internal var isWhileListed: Boolean = false,
        internal var maxPlayer: Int = 20,
        internal var viewDistance: Int = 10,
        internal var allowNether: Boolean = true,
        internal var spawnProtectRange: Int = 10,
        internal var jvmFlagTemplate: String = "none",
        internal var anotherJVMFlags: String = "",
        internal var allowGUI: Boolean = false,
        internal var minimumAllocatedMemory: Int = 512,
        internal var maximumAllocatedMemory: Int = 512,
    )

}