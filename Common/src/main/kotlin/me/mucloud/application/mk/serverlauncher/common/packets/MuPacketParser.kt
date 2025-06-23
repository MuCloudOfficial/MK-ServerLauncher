package me.mucloud.application.mk.serverlauncher.common.packets

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.MCJEServer
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.MCJEServerAdapter
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.ServerType
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.ServerTypeSerializer

object MuPacketParser {

    fun parseFromJson(json: JsonObject): MuPacket {
        val type = json["type"].asString.split(":")
        val data = json["data"].asJsonObject
        return when(type[0]){
            "msg.in" -> {
                getGson().fromJson(data, MuSend2ServerPacket::class.java)
            }

            "config" -> {
                getGson().fromJson(data, MuServerConfigPacket::class.java)
            }

            else -> throw JsonParseException("")
        }
    }

}

fun initGson(): GsonBuilder{
    val factory =
        RuntimeTypeAdapterFactory
            .of(MuPacket::class.java, "type")
            .registerSubtype(MuServerConfigPacket::class.java, "config")
            .registerSubtype(MuSend2ServerPacket::class.java, "msg.in:send")
            .registerSubtype(MuBroadcastPacket::class.java, "msg.in:broadcast")
            .registerSubtype(MuServerInfoPacket::class.java, "server:info")
            .registerSubtype(MuServerStartPacket::class.java, "server:start")
            .registerSubtype(MuServerStopPacket::class.java, "server:stop")

    return GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(MCJEServer::class.java, MCJEServerAdapter)
        .registerTypeAdapter(ServerType::class.java, ServerTypeSerializer)
        .registerTypeAdapterFactory(factory)
}

fun getGson(): Gson = initGson().create()