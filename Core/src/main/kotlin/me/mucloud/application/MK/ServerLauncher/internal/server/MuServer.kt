package me.mucloud.application.MK.ServerLauncher.internal.server

import me.mucloud.application.MK.ServerLauncher.internal.env.MuEnvironment
import java.io.File
import java.time.LocalDateTime

/**
 * A Server.
 *
 * Only API.
 *
 * ! Internal, may not implement !
 *
 * @since DEV.16
 * @author Mu_Cloud
 */
interface MuServer {
    fun start()
    fun stop()
    fun isRunning(): Boolean
    fun totalFailCount(): Int
    fun totalPassCount(): Int
    fun getName(): String
    fun changeName(name: String)
    fun setDescription(desc: String)
    fun getPort(): Int
    fun getVersion(): String
    fun getType(): String
    fun lastLaunchTime(): LocalDateTime
    fun getFolder(): File
    fun getEnv(): MuEnvironment
}