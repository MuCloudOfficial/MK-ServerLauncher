package me.mucloud.application.MK.ServerLauncher.internal.protocol.packets

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

interface MuPacket: JsonSerializer<MuPacket>, JsonDeserializer<MuPacket> {

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

    /**
     * # | MuPacket 序列化
     *
     * ## !! 不应直接实现该方法，如非必要，请优先实现变量 [data]
     *
     * @see data
     * @since DEV.1
     * @author Mu_Cloud
     */
    override fun serialize(
        s: MuPacket,
        t: Type,
        c: JsonSerializationContext
    ): JsonElement = JsonObject().apply {
        addProperty("type", "$id:$operation")
        add("data", data)
    }

    /**
     * # | MuPacket 反序列化
     *
     * 实现该方法以提供基于 MuPacket 数据的反序列化实现
     *
     * @since DEV.1
     * @author Mu_Cloud
     */
    override fun deserialize(
        j: JsonElement,
        t: Type,
        c: JsonDeserializationContext
    ): MuPacket

}

