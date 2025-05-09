package me.mucloud.application.MK.ServerLauncher.internal.server.mcserver

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import me.mucloud.application.MK.ServerLauncher.internal.env.JavaEnvironment
import java.io.File
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
    private val type: String,
    private var desc: String,
    private val location: String,
    private var port: Int = 25565,
    @Contextual private var env: JavaEnvironment,
){

    @Contextual var running: Boolean = false
    val totalFailCount: Int = 0
    val totalPassCount: Int = 0

    fun start() {
        running = true
    }

    fun stop() {
        running = false
    }

    fun getName(): String = name
    fun isRunning(): Boolean = running
    fun totalFailCount(): Int = totalFailCount
    fun totalPassCount(): Int = totalPassCount
    fun setName(name: String) { this.name = name }
    fun setDescription(desc: String){ this.desc = desc }
    fun getFolder(): File = TODO()
    fun getPort(): Int = port
    fun setPort(port: Int) { this.port = port }
    fun getVersion(): String = version
    fun getType(): String = type
    fun getEnv(): JavaEnvironment = env
    fun setEnv(env: JavaEnvironment) { this.env = env }
    fun lastLaunchTime(): LocalDateTime = LocalDateTime.now() //todo
}