package me.mucloud.application.mk.serverlauncher.mupacket.api

import com.google.gson.JsonObject

/**
 * # MuPacket API
 *
 * ## MuPacket Info
 *
 * MuPacket PID & Deserializer Provider
 *
 * It should be Defined if you want to register the SubClasses of MuPacket
 *
 * @since TinyNova V1 | DEV.2
 * @author Mu_Cloud
 */
interface MuPacketInfo<T : MuPacket> {
    val pid: String
    fun fromData(data: JsonObject): T
}
