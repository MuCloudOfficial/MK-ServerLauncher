package me.mucloud.application.mk.serverlauncher.mupacket.api

import com.google.gson.JsonObject
import java.util.concurrent.ConcurrentHashMap

/**
 * # MuPacket API
 *
 * ## MuPacketFactory
 *
 * MuPacket Register & MuPacket Deserializer
 *
 * @since RainyZone V1 | DEV.1
 * @author Mu_Cloud
 */
object MuPacketFactory {
    private val pattern: Regex = Regex("^mu(core|view)(\\.[A-Za-z0-9\\-_]{2,})+:([A-Za-z0-9\\-_]{2,})$")
    private val MPPool: ConcurrentHashMap<String, MuPacketInfo<out MuPacket>> = ConcurrentHashMap()

    fun <T : MuPacket> regMuPacket(type: MuPacketInfo<T>) {
        val pid = type.pid
        require(!MPPool.containsKey(pid)) { "Invalid MuPacket ID: $pid >> Ambiguous MP_ID" }
        require(pattern.matches(pid)) { "Invalid MuPacket ID: $pid >> Ambiguous MP_ID" }
        MPPool[pid] = type
    }

    fun toPacket(raw: JsonObject): MuPacket {
        check(raw.has("MP_ID") && raw.has("MP_DATA")) { "Invalid MuPacket Raw >> Corrupted Raw" }
        val mpid = raw["MP_ID"].asString
        val type = MPPool[mpid] ?: error("Invalid MuPacket Raw >> Unregistered MP_ID ($mpid)")
        val data = raw["MP_DATA"].asJsonObject
        return type.fromData(data)
    }
}