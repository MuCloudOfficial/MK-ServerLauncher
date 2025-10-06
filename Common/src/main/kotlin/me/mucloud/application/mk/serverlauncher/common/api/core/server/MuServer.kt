package me.mucloud.application.mk.serverlauncher.common.api.core.server

import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableSharedFlow
import me.mucloud.application.mk.serverlauncher.common.api.core.config.MuProperties
import me.mucloud.application.mk.serverlauncher.common.api.core.env.MuEnvironment
import me.mucloud.application.mk.serverlauncher.common.api.packet.MuPacket
import java.io.File
import java.time.LocalDateTime

/**
 *  # | MuServer
 *
 *  一个服务器接口 | 如非必要，请不要直接实现这个接口，请转向实现 AbstractMuServer
 *
 *  @see AbstractMuServer
 *  @author Mu_Cloud
 *  @since Hyper MPE Mini | V1
 */
interface MuServer {

    /**
     *  获取服务器名
     */
    fun getName(): String

    /**
     *  更改服务器名
     */
    fun setName(name: String): Boolean

    /**
     *  获取服务器名
     */
    fun getPort(): Int

    /**
     *  设置服务器端口
     */
    fun setPort(port: Int)

    /**
     *  获取服务器描述
     */
    fun getDescription(): String

    /**
     *  设置服务器描述
     */
    fun setDescription(description: String)

    /**
     *  获取服务器版本
     */
    fun getVersion(): String

    /**
     *  更改服务器版本
     */
    fun setVersion(version: String)

    /**
     *  服务器初次部署
     */
    fun deploy()

    /**
     *  获取服务器是否在运行
     */
    fun getStatus(): MuServerStatus

    /**
     *  启动服务器
     */
    fun start()

    /**
     *  停止服务器
     */
    fun stop()

    /**
     *  重启服务器
     */
    fun restart()

    /**
     *  获取服务器设置
     */
    fun getConfiguration(): MuProperties

    /**
     * 添加服务器启动前任务
     */
    fun addBeforeWork(work: String)

    /**
     * 添加服务器启动前任务
     */
    fun addBeforeWork(vararg work: String)

    /**
     * 启动服务器运行前任务
     */
    fun runBeforeWorks(): Boolean

    /**
     * 获取服务器运行前任务
     */
    fun getBeforeWorks(): List<String>

    /**
     * Get Start Command
     */
    fun getStartCmd(): String

    /**
     * 获取服务器信息流
     */
    fun getDataFlow(): MutableSharedFlow<MuPacket>

    /**
     * 获取该服务器使用的环境
     */
    fun getEnv(): MuEnvironment

    /**
     * 设置该服务器所使用的环境
     */
    fun setEnv(env: MuEnvironment)

    /**
     * 获取该服务器的最后一次启动时间
     */
    fun lastLaunchTime(): LocalDateTime

    /**
     * 获取该服务器存放的文件夹
     */
    fun getFolder(): File

    /**
     * 生成 MK-ServerLauncher.json 文件并写入 MuServer JSON 化信息
     */
    fun saveToFile()

    fun serialize(): JsonObject

    fun deserialize(json: JsonObject)
}