package me.mucloud.application.MK.ServerLauncher.internal.server

import me.mucloud.application.MK.ServerLauncher.internal.env.JavaEnvironment
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.MCJEServer

object ServerPool {

    private val AvailableType = mutableMapOf(
        "paper" to "Paper & PaperSpigot",
        "folia" to "Folia"
    )

    private val Pool: MutableList<MCJEServer> = mutableListOf(
        MCJEServer("TestServer", "1.19.2", "paper", "", 25566, JavaEnvironment("", ""), MCJEServer.Config())
    )

    internal fun addServer(server: MCJEServer){ Pool.add(server) }
    internal fun delServer(server: MCJEServer){ Pool.remove(server) }
    internal fun getServer(name: String): MCJEServer? = Pool.find { name == it.getName() }
    internal fun getServerList(): List<MCJEServer> = Pool
    internal fun getTotalServer(): Int = Pool.size
    internal fun getOnlineServerCount(): Int = Pool.filter { it.isRunning() }.size
    internal fun getOfflineServerCount(): Int = Pool.filter { !it.isRunning() }.size
    internal fun getAvailableType() = AvailableType

}


