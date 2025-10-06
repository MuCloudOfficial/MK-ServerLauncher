package me.mucloud.application.mk.serverlauncher.common.api.core.server

import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import me.mucloud.application.mk.serverlauncher.common.api.packet.MuPacket
import me.mucloud.application.mk.serverlauncher.common.utils.MUSV_LOGGER
import java.io.File
import java.io.FileWriter
import java.nio.charset.StandardCharsets

abstract class AbstractMuServer(
    private val name: String,
    private val port: Int,
    private val description: String,
    private val beforeWorks: List<String>,
): MuServer {

    private val dataFlow = MutableSharedFlow<MuPacket>()
    private var status: MuServerStatus = MuServerStatus.STOPPED
    private val LOGGER = MUSV_LOGGER(this)

    final override fun start() {
        LOGGER.info("Starting")
        status = MuServerStatus.PREPARING
        runBeforeWorks()
        runStartCmd()
    }

    final override fun stop() {
        LOGGER.info("Stopping")
        status = MuServerStatus.STOPPING
        sendStopSignal()
    }

    final override fun restart() {
        stop()
        start()
    }

    final override fun getDataFlow(): MutableSharedFlow<MuPacket> = dataFlow

    override fun saveToFile() {
        FileWriter(File(getFolder(), "MK-ServerLauncher.json").also { if(!it.exists()) it.createNewFile() }, StandardCharsets.UTF_8).also {
            it.write(GsonBuilder().setPrettyPrinting().create().toJson(this))
            it.flush()
        }
    }

    final override fun runBeforeWorks(): Boolean {
        getBeforeWorks().forEach { be ->
            try {
                Runtime.getRuntime().exec(be).errorStream.bufferedReader().forEachLine { l ->
                    runBlocking {

                    }
                }
            }catch (e: Exception) {
                LOGGER.error("MuServer(${getName()}) caught an Exception", e)
                return false
            }
        }
        return true
    }

    private fun runStartCmd(){ Runtime.getRuntime().exec(getStartCmd()) }

    // Define Before Works (In PREPARE Status)
    abstract override fun getBeforeWorks(): List<String>
    abstract fun sendStopSignal()

}