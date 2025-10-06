package me.mucloud.application.mk.serverlauncher.common.api.packet

import com.google.gson.JsonObject
import me.mucloud.application.mk.serverlauncher.common.utils.CORE_LOGGER

abstract class MuErrPacket(
    private val throws: Throwable,
): MuPacket {
    override fun getPID(): String = "MP_${getType()}_${hashCode().toString(8)}"

    override fun asJson(): JsonObject = JsonObject().apply {
        addProperty("pid", getPID())
        addProperty("type", getType().toString())
        add("throws", JsonObject().apply {
            addProperty("cause", getThrows().javaClass.name)
            addProperty("message", getThrows().message)
        })
    }

    override fun asJsonString(): String = asJson().asString

    override fun process() {
        CORE_LOGGER.error("")
        postProcess()
    }

    fun getThrows(): Throwable = throws

    abstract fun postProcess()

}