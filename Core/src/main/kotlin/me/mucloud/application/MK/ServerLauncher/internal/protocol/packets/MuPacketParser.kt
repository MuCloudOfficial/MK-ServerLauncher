package me.mucloud.application.MK.ServerLauncher.internal.protocol.packets

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import me.mucloud.application.MK.ServerLauncher.getGson
import me.mucloud.application.MK.ServerLauncher.initGson
import me.mucloud.application.MK.ServerLauncher.internal.server.ServerPool

object MuPacketParser {

    fun parseFromJson(json: JsonObject): MuPacket{
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