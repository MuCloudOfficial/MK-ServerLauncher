package me.mucloud.application.mk.serverlauncher.common.packets

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

interface MuPacket {

    /**
     * # | MuPacket ID
     *
     * 用于指定 MuPacket 的 ID
     *
     * MuPacket ID 用于区分 MuPacket 的功能
     *
     * @since DEV.1
     * @author Mu_Cloud
     */
    val id: String
    val operation: String

    /**
     * # | MuPacket 数据
     *
     * ## !! 优先实现该元素 !!
     *
     * @since DEV.1
     * @author Mu_Cloud
     */
    val data: JsonObject

    fun deserialize(
        j: JsonObject
    ): MuPacket

}

object MuPacketFactory: JsonSerializer<MuPacket>, JsonDeserializer<MuPacket>{

    private val ReflectPool = mutableMapOf(
        "config" to MuServerConfigPacket::class.java,
        "msg.in" to MuSend2ServerPacket::class.java,
        "server:info" to MuServerInfoPacket::class.java,
        "server:start" to MuServerStartPacket::class.java,
        "server:stop" to MuServerStopPacket::class.java,
    )

    private fun reflectPacket(type: String): Class<out MuPacket> =
        (ReflectPool[type] ?: throw UnsupportedOperationException("Unsupported MuPacket Type"))

    override fun serialize(
        m: MuPacket,
        t: Type,
        c: JsonSerializationContext
    ): JsonElement = JsonObject().apply{
        addProperty("type", "${m.id}:${m.operation}")
        add("data", m.data)
    }

    override fun deserialize(
        j: JsonElement,
        t: Type,
        c: JsonDeserializationContext
    ): MuPacket = //todo
        reflectPacket(j.asJsonObject["type"].asString).kotlin.objectInstance!!.deserialize(j.asJsonObject["data"].asJsonObject)

}

