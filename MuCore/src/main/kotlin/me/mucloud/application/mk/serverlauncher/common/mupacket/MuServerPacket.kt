package me.mucloud.application.mk.serverlauncher.common.mupacket

import com.google.gson.JsonObject
import me.mucloud.application.mk.serverlauncher.common.mupacket.api.AbstractMuPacket
import me.mucloud.application.mk.serverlauncher.common.server.MCJEServer

abstract class MuServerPacket(
    val targetServer: MCJEServer,
): AbstractMuPacket() {

    final override val id: String = "muserver"

    fun getServer() = targetServer

    final override fun getData(): JsonObject = JsonObject().apply{
        addProperty("SV_NAME", targetServer.getName())
        add("SV_OP", getMSPData())
    }

    abstract fun getMSPData(): JsonObject
    abstract override fun operate(): Boolean

}