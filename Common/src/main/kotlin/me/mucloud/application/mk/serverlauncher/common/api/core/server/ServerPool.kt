package me.mucloud.application.mk.serverlauncher.common.api.core.server

import java.io.File

object ServerPool {

    private val ServerTypePool = mutableListOf<MuServerType>()
    private val Pool: MutableList<MuServer> = mutableListOf()

    fun addServer(server: MuServer){ Pool.add(server); server.saveToFile() }
    fun MuServer.delete(){ getFolder().deleteRecursively(); Pool.remove(this) }
    fun MuServer.remove(){ File(getFolder(), "MK-ServerLauncher.json").delete(); Pool.remove(this) }
    fun getServer(name: String): MuServer? = Pool.find { name == it.getName() }
    fun getServerList(): List<MuServer> = Pool
    fun getTotalServer(): Int = Pool.size
    fun getOnlineServerCount(): Int = Pool.filter { it.getStatus() == MuServerStatus.RUNNING }.size
    fun getOfflineServerCount(): Int = Pool.filter { it.getStatus() == MuServerStatus.STOPPED }.size
    fun getAvailableTypes() = ServerTypePool
    fun scanServer(){
        //todo
    }

    fun saveServers(){ Pool.forEach { it.saveToFile() } }
}


