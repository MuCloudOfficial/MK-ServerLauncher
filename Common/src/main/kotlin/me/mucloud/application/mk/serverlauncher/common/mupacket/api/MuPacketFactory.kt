package me.mucloud.application.mk.serverlauncher.common.mupacket.api

import com.google.gson.JsonObject
import me.mucloud.application.mk.serverlauncher.common.external.MuLogger.err
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.jvmName

object MuPacketFactory {
    private val pattern: Regex = Regex("^mucore:([A-Za-z0-9\\-_]{3,}):([A-Za-z0-9\\-_]{3,})$")
    private val MPPool: ConcurrentHashMap<String, KClass<out AbstractMuPacket>> = ConcurrentHashMap()

    fun regMuPacket(mpClass: KClass<out AbstractMuPacket>) {
        // Check mpClass is Not Abstract Class
        if (mpClass.isAbstract) {
            throw UnsupportedOperationException().also {
                err("UnSupport Class Type ${mpClass.jvmName}: Abstract MuPacket is not supported", it)
            }
        }
        val mpid = mpClass.createInstance().getPID()
        if(!pattern.matches(mpid)){
            throw UnsupportedOperationException().also {
                err("Invalid PID was used when creating a MuPacket.", it)
            }
        }
        if(MPPool.containsKey(mpid)) {
            throw UnsupportedOperationException().also {
                err("Exist MuPacket ID: $mpid", it)
            }
        }
        MPPool[mpid] = mpClass
    }

    fun toPacket(raw: JsonObject): MuPacket {
        try {
            if (!raw.has("MP_ID") || !raw.has("MP_DATA")) {
                throw UnsupportedOperationException("Detected Format Error in Received MuPacket RawJsonMessage Recently.")
            }
            val mpid = raw.get("MP_ID").asString
            if (!MPPool.containsKey(mpid)) {
                throw UnsupportedOperationException("This MP_ID: $mpid does not Registered in MuPacketAPI.")
            }
            val targetMPClass = MPPool[mpid]!!
            val targetMP = targetMPClass.createInstance()
            val mpData = raw.getAsJsonObject("MP_DATA")
            return targetMP.toPacket(mpData) ?: throw UnsupportedOperationException("Illegal MP_DATA for MuPacket ($mpid)")
        }catch (e: Exception){
            err("Error Occurred when reading MuPacket", e)
            throw e
        }
    }

}