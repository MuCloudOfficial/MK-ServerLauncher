package me.mucloud.application.MK.ServerLauncher.internal.server.mcserver

import kotlinx.serialization.Serializable
import me.mucloud.application.MK.ServerLauncher.internal.env.MuEnvironment
import me.mucloud.application.MK.ServerLauncher.internal.server.MuServer
import java.io.File
import java.time.LocalDateTime

/**
 * An Abstract Server
 *
 * Can Extend some Dedicated Server Template Pack by extend this Class
 *
 * such as Minecraft Server, Minecraft Proxy or some like that.
 *
 * @since DEV.16
 * @author Mu_Cloud
 */

@Serializable
abstract class AbstractServer(
    private var name: String,
    private var desc: String,
    private var port: Int,
    private var location: String,
    private var version: String,
    private var serverType: String,
    private var env: MuEnvironment,
): MuServer {

    private var running = false
    private var totalFailCount = 0

    abstract override fun start()
    abstract override fun stop()

    override fun getName(): String = name
    override fun isRunning(): Boolean = running
    override fun totalFailCount(): Int = totalFailCount
    override fun changeName(name: String) { this.name = name }
    override fun setDescription(desc: String){ this.desc = desc }
    override fun getFolder(): File = File(location).parentFile
    override fun getPort(): Int = port
    override fun getVersion(): String = version
    override fun getType(): String = serverType
    override fun getEnv(): MuEnvironment = env
    override fun lastLaunchTime(): LocalDateTime = LocalDateTime.now() //todo

}