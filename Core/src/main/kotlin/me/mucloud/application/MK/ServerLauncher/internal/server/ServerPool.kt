package me.mucloud.application.MK.ServerLauncher.internal.server

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.AbstractServer

object ServerPool {

    private val POOL: MutableList<AbstractServer> = mutableListOf()

    fun addServer(server: AbstractServer){
        POOL.add(server)
    }

    fun delServer(server: AbstractServer){
        POOL.remove(server)
    }

    fun getServerList(): List<AbstractServer> = POOL

    fun getSize(): Int = POOL.size

    fun sendOverview(): JsonElement{ // Test
        return JsonObject().also { root ->
            root.add("AppInfo", JsonObject().also { appinfo ->
                appinfo.addProperty("DEV", 1)
                appinfo.addProperty("REL", 1)
                appinfo.addProperty("VersionName", "VoidLand V1")
            })
            root.add("MuServer", JsonObject().also { server ->
                server.addProperty("Total", 19)
                server.addProperty("Running", 5)
            })
        }
    }

}