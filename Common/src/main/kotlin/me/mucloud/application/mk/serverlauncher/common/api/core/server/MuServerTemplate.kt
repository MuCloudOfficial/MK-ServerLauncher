package me.mucloud.application.mk.serverlauncher.common.api.core.server

import me.mucloud.application.mk.serverlauncher.common.api.core.config.MuProperty
import me.mucloud.application.mk.serverlauncher.common.api.packet.MuPacket

interface MuServerTemplate {

    fun getProperties(): List<MuProperty<*>>
    fun asMuPacket()
    fun validate(packet: MuPacket)

}