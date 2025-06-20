package me.mucloud.application.mk.serverlauncher.common.packets

import com.google.gson.JsonObject
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.MCJEServer

/**
 * # | MuPacket SendToServer 传输包
 *
 * ## 该包用于向玩家发送消息
 *
 * 基于动态的消息已在计划内
 *
 * @param target 发送目标，只能是服务器中的在线玩家，离线玩家的接收消息正在实现中
 * @param msg 消息内容，支持 Placeholder API 与自定义颜色, 其它的文字支持正在实现中
 * @since DEV.1
 * @author Mu_Cloud
 */
class MuSend2ServerPacket(
    targetServer: MCJEServer,
    target: String,
    msg: String
): MuPacket {
    
    override val id: String = "msg.in"
    override val operation: String = "send"
    override val data: JsonObject = JsonObject().apply{
        addProperty("target_server", targetServer.getName())
        addProperty("target", target)
        addProperty("msg", msg)
    }

    override fun deserialize(j: JsonObject): MuPacket {
        TODO()
    }

}


class MuBroadcastPacket(
    targetServer: MCJEServer,
    msg: String
): MuPacket {
    override val id: String = "msg.in"
    override val operation: String = "broadcast"
    override val data: JsonObject = JsonObject().apply {
        addProperty("target_server", targetServer.getName())
        addProperty("msg", msg)
    }

    override fun deserialize(j: JsonObject): MuPacket {
        TODO("Not yet implemented")
    }

}