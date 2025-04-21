package me.mucloud.application.MK.ServerLauncher.internal.server

import me.mucloud.application.MK.ServerLauncher.internal.env.EnvPool
import me.mucloud.application.MK.ServerLauncher.internal.env.JavaEnvironment
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.AbstractServer
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.MCJEServer

object ServerPool {

    private val POOL: MutableList<AbstractServer> = mutableListOf(
        MCJEServer("MuServer1", "1.16.5", "Paper", "No Desc", "/", 25565, EnvPool.getEnv("zulu11") as JavaEnvironment, running = true),
        MCJEServer("MuServer2", "1.19.2", "Paper", "No Desc", "/", 25666, EnvPool.getEnv("zulu16") as JavaEnvironment)
    )

    fun addServer(server: AbstractServer){
        POOL.add(server)
    }

    fun delServer(server: AbstractServer){
        POOL.remove(server)
    }

    fun getServer(name: String): AbstractServer?{
        POOL.forEach {
            if (it.getName() == name){

                return it
            }
        }
        return null
    }

    fun getServerList(): List<AbstractServer> = POOL
    fun getTotalServer(): Int = POOL.size
    fun getOnlineServerCount(): Int = POOL.filter { it.isRunning() }.size
    fun getOfflineServerCount(): Int = POOL.filter { !it.isRunning() }.size

}