package me.mucloud.application.mk.serverlauncher.common.api.packet

import com.google.gson.JsonObject

interface MuPacket {

    fun getPID(): String
    fun getType(): MuPacketType
    fun asJson(): JsonObject
    fun asJsonString(): String
    fun process()

}