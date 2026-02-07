package me.mucloud

import com.google.gson.JsonObject
import me.mucloud.application.mk.serverlauncher.core.external.MuLogger.info
import me.mucloud.application.mk.serverlauncher.mupacket.api.AbstractMuPacket
import me.mucloud.application.mk.serverlauncher.mupacket.api.MuPacketFactory
import kotlin.test.Test

class MuMsgPacket(): AbstractMuPacket(){

    private lateinit var msg: String
    override val id: String = "mumsg"
    override val operation: String = "info"

    constructor(msg:String): this(){
        this.msg = msg
    }

    override fun getData(): JsonObject = JsonObject().apply{
        addProperty("msg", msg)
    }

    override fun operate(): Boolean {
        info(msg)
        return true
    }

    override fun toPacket(mpdata: JsonObject): MuMsgPacket? =
        if(mpdata.has("msg")){
            MuMsgPacket(mpdata.get("msg").asString)
        }else{
            null
        }
}


class ApplicationTest {

    @Test
    fun test(){
        MuPacketFactory.regMuPacket(MuMsgPacket::class)
        MuMsgPacket("Hello World!").operate()

        val json = JsonObject().apply {
            addProperty("MP_ID", "mucore:mumsg:info")
            add("MP_DATA", JsonObject().apply {
                addProperty("msg", "Hi, MuCore! MuView Here!")
            })
        }
        MuPacketFactory.toPacket(json).operate()
    }

}
