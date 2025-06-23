package me.mucloud.application.mk.serverlauncher.common.packets

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.MCJEServer
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool

class MuServerStartPacket(
    private val target: MCJEServer,
): MuPacket {
    override val id: String = "server"
    override val operation: String = "start"
    override val data: JsonObject = JsonObject().apply {
        addProperty("server", target.getName())
    }

    override fun deserialize(j: JsonObject): MuPacket =
        MuServerStartPacket(ServerPool.getServer(j["target_server"].asString) ?: throw JsonParseException("Server Not Found"))

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

    override fun deserialize(j: JsonObject): MuPacket =
        MuServerStartPacket(ServerPool.getServer(j["target_server"].asString) ?: throw JsonParseException("Server Not Found"))

    fun getServer() = target
}

class MuServerInfoPacket(
    private val targetServer: MCJEServer
): MuPacket {
    override val id: String = "server"
    override val operation: String = "info"
    override val data: JsonObject = getGson().toJsonTree(targetServer).asJsonObject

    override fun deserialize(j: JsonObject): MuPacket =
        MuServerInfoPacket(getGson().fromJson(j.asJsonObject["data"].asJsonObject["target_server"], MCJEServer::class.java))

    fun getServer() = targetServer
}

class MuServerConfigPacket(
    val type: ConfigOperationType,
    val key: String,
    val value: String?,
): MuPacket {
    override val id: String = "config"
    override val operation: String = type.name.lowercase()
    override val data: JsonObject = JsonObject().apply {
        addProperty("key", key)
        if(type != ConfigOperationType.DEL){
            addProperty("value", value)
        }
    }

    override fun deserialize(j: JsonObject): MuPacket =
        MuServerConfigPacket(
            ConfigOperationType.valueOf(j["type"].asString.uppercase()),
            j["key"].asString,
            j["value"].asString
        )

    enum class ConfigOperationType{
        ADD, DEL, MOD;

        override fun toString(): String = name.lowercase()
    }
}


