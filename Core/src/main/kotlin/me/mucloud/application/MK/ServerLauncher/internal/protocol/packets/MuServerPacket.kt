package me.mucloud.application.MK.ServerLauncher.internal.protocol.packets

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import me.mucloud.application.MK.ServerLauncher.getGson
import me.mucloud.application.MK.ServerLauncher.internal.server.ServerPool
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.MCJEServer

class MuServerStartPacket(
    private val target: MCJEServer,
): MuPacket {
    override val id: String = "server"
    override val operation: String = "start"
    override val data: JsonObject = JsonObject().apply {
        addProperty("server", target.getName())
    }

    override fun deserialize(
        j: JsonElement,
        t: Type,
        c: JsonDeserializationContext
    ): MuPacket =
        MuServerStartPacket(ServerPool.getServer(j.asJsonObject["data"].asJsonObject["server"].asString) ?: throw JsonParseException("Server Not Found"))

    fun getServer() = target
}

class MuServerStopPacket(
    private val target: MCJEServer,
): MuPacket {
    override val id: String = "server"
    override val operation: String = "stop"
    override val data: JsonObject = JsonObject().apply {
        addProperty("target_server", target.getName())
    }

    override fun deserialize(
        j: JsonElement,
        t: Type,
        c: JsonDeserializationContext
    ): MuPacket =
        MuServerStartPacket(ServerPool.getServer(j.asJsonObject["data"].asJsonObject["server"].asString) ?: throw JsonParseException("Server Not Found"))

    fun getServer() = target
}

class MuServerInfoPacket(
    private val targetServer: MCJEServer
): MuPacket{
    override val id: String = "server"
    override val operation: String = "info"
    override val data: JsonObject = getGson().toJsonTree(targetServer).asJsonObject

    override fun deserialize(
        j: JsonElement,
        t: Type,
        c: JsonDeserializationContext
    ): MuPacket =
        MuServerInfoPacket(getGson().fromJson(j.asJsonObject["data"].asJsonObject["target_server"], MCJEServer::class.java))

    fun getServer() = targetServer
}

class MuServerConfigPacket(
    val type: ConfigOperationType,
    val key: String,
    val value: String?,
): MuPacket{
    override val id: String = "config"
    override val operation: String = type.name
    override val data: JsonObject = JsonObject().apply {
        addProperty("key", key)
        if(type != ConfigOperationType.DEL){
            addProperty("value", value)
        }
    }

    override fun deserialize(
        j: JsonElement,
        t: Type,
        c: JsonDeserializationContext
    ): MuPacket {
        val data = j.asJsonObject["data"]
        return MuServerConfigPacket(
            ConfigOperationType.valueOf(data.asJsonObject["type"].asString.uppercase()),
            data.asJsonObject["key"].asString,
            data.asJsonObject["value"].asString
        )
    }

    enum class ConfigOperationType{
        ADD, DEL, MOD;

        override fun toString(): String = name.lowercase()

    }
}


