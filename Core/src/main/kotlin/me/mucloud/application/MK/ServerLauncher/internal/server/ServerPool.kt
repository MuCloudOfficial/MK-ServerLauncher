package me.mucloud.application.MK.ServerLauncher.internal.server

import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.MCJEServer

object ServerPool {

    private val Pool: MutableList<MCJEServer> = mutableListOf()

    fun addServer(server: MCJEServer){ Pool.add(server) }
    fun delServer(server: MCJEServer){ Pool.remove(server) }
    fun getServer(name: String): MCJEServer? = Pool.find { name == it.getName() }
    fun getServerList(): List<MCJEServer> = Pool
    fun getTotalServer(): Int = Pool.size
    fun getOnlineServerCount(): Int = Pool.filter { it.isRunning() }.size
    fun getOfflineServerCount(): Int = Pool.filter { !it.isRunning() }.size
}

