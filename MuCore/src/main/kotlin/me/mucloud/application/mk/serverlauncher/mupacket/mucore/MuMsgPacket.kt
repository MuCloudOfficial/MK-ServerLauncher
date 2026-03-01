package me.mucloud.application.mk.serverlauncher.mupacket.mucore

import com.google.gson.JsonObject
import me.mucloud.application.mk.serverlauncher.mupacket.api.AbstractMuPacket
import me.mucloud.application.mk.serverlauncher.mupacket.api.MuPacketInfo

class MuMsgPacket(
    val status: MuStatus,
    val msg: String,
): AbstractMuPacket(
    object: MuPacketInfo<MuMsgPacket> {
        override val pid: String = "mucore.internal:msg"
        override fun fromData(data: JsonObject): MuMsgPacket {
            val status = data.get("status").asString ?: error("Exception occurred while parsing MuPacket >> Invalid MuStatus")
            val msg = data.get("msg").asString ?: error("Exception occurred while parsing MuPacket >> Null MSG")
            return MuMsgPacket(MuStatus.valueOf(status), msg)
        }
    }
){
    override fun getData(): JsonObject = JsonObject().apply{
        addProperty("status", status.name)
        addProperty("msg", msg)
    }
}