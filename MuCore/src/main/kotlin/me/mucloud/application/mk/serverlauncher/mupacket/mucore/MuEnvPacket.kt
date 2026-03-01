package me.mucloud.application.mk.serverlauncher.mupacket.mucore

import com.google.gson.JsonObject
import me.mucloud.application.mk.serverlauncher.muenv.JavaEnvironment
import me.mucloud.application.mk.serverlauncher.mupacket.api.AbstractMuPacket
import me.mucloud.application.mk.serverlauncher.mupacket.api.MuPacketInfo

abstract class MuEnvPacket(
    val targetJEnv: JavaEnvironment,
    type: MuPacketInfo<*>,
) : AbstractMuPacket(type) {

    fun getEnv(): JavaEnvironment = targetJEnv

    final override fun getData(): JsonObject = JsonObject().apply {
        addProperty("EV_NAME", targetJEnv.name)
        add("EV_OP", getMEPData())
    }

    abstract fun getMEPData(): JsonObject
}