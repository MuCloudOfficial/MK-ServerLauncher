package me.mucloud.application.mk.serverlauncher.common.api

/**
 *  # | MuServer
 *
 *  一个服务器接口 | 如非必要，请不要直接实现这个接口，请转向实现 AbstractMuServer
 *
 *  @see me.mucloud.application.mk.serverlauncher.common.server.AbstractMuServer
 *  @author Mu_Cloud
 *  @since Hyper MPE Mini | V1
 */
interface MuServer {

    fun getName(): String

    fun getDefaultPort(): Int

    fun getPort(): Int

    fun setPort(port: Int)

    fun getDescription(): String

    fun setDescription(description: String)

    fun getVersion(): String

    fun setVersion(version: String)

    fun start()

    fun stop()

    fun restart()

    fun getConfiguration(): MuServerConfiguration

    fun runBeforeWorks(): Boolean
}