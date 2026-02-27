package me.mucloud.application.mk.serverlauncher.mupacket.api

import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * # MuPacket API
 *
 * The Core Concept of MuPacketAPI
 *
 * @since RainyZone V1 | DEV.1
 * @suppress Incubate API, and may be changed in the future. Do not implement directly! Please extend [AbstractMuPacket]
 * @see AbstractMuPacket
 * @author Mu_Cloud
 */
interface MuPacket {
    /**
     * MuPacket Customized Data
     *
     * Define the MP_DATA value in [JsonObject] from [toJson]
     *
     * @return The Serialized MuPacket Custom Data as [JsonElement]
     * @see toJson
     * @since RainyZone V1 | DEV.1
     */
    fun getData(): JsonObject

    /**
     * MuPacket ID
     *
     * Define the MP_ID value in [JsonObject] from [toJson]
     *
     * @return The MuPacket ID
     * @see toJson
     * @since RainyZone V1 | DEV.1
     */
    fun getPID(): String

    /**
     * MuPacket2JSON
     *
     * Basic Structure
     *
     * MP_ID: MuPacket ID
     *
     * MP_DATA: Customized MuPacket from [getData]
     *
     * @return The Serialized MuPacket as [JsonObject]
     * @see getData
     * @since RainyZone V1 | DEV.1
     */
    fun toJson(): JsonObject

    /**
     * MuPacket Timestamp
     *
     * @return The created Time of MuPacket as Millis
     * @since RainyZone V1 | DEV.1
     */
    fun getTimestamp(): Long

    /**
     * MuPacket4Json: MP_DATA Converter
     *
     * @param mpdata The MP_DATA content from Json Data by [MuPacket.toJson]
     * @return A [MuPacket] that loaded the MP_DATA json data
     * @since RainyZone V1 | DEV.1
     */
    fun toPacket(mpdata: JsonObject): MuPacket?

}