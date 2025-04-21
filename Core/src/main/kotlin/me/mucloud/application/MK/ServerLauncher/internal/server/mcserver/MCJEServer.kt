package me.mucloud.application.MK.ServerLauncher.internal.server.mcserver

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import me.mucloud.application.MK.ServerLauncher.internal.env.JavaEnvironment

/**
 * MC Java Edition Server.
 *
 * MuPack Internal Server Template
 *
 * @since DEV.16
 * @author Mu_Cloud
 */
@Serializable
data class MCJEServer(
    private val mcs_name: String,
    private val mcs_version: String,
    private val mcs_type: String,
    private val mcs_desc: String,
    private val mcs_location: String,
    private val mcs_port: Int = 25565,
    @Contextual private val mcs_env: JavaEnvironment,
    @Contextual override var running: Boolean = false
): AbstractServer(mcs_name,
    mcs_desc,
    mcs_port,
    mcs_location,
    mcs_version,
    "Minecraft Java Server",
    mcs_env,
    ) {
    override val totalFailCount: Int = 0
    override val totalPassCount: Int = 0

    override fun start() {
        running = true
    }

    override fun stop() {
        running = false
    }
}