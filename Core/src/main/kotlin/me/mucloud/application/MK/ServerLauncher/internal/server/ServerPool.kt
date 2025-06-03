package me.mucloud.application.MK.ServerLauncher.internal.server

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import me.mucloud.application.MK.ServerLauncher.internal.manage.Configuration
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.MCJEServer
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.MCJEServerAdapter
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.ServerType
import me.mucloud.application.MK.ServerLauncher.log
import java.io.File
import java.io.FileReader
import java.nio.charset.StandardCharsets

object ServerPool {

    private val ServerTypePool = mutableListOf<ServerType>()

    private val Pool: MutableList<MCJEServer> = mutableListOf()

    internal fun addServer(server: MCJEServer){ Pool.add(server); server.saveToFile() }
    internal fun MCJEServer.delete(){ getFolder().deleteRecursively(); Pool.remove(this) }
    internal fun MCJEServer.remove(){ File(getFolder(), "MK-ServerLauncher.json").delete(); Pool.remove(this) }
    internal fun getServer(name: String): MCJEServer? = Pool.find { name == it.getName() }
    internal fun getServerList(): List<MCJEServer> = Pool
    internal fun getTotalServer(): Int = Pool.size
    internal fun getOnlineServerCount(): Int = Pool.filter { it.isRunning() }.size
    internal fun getOfflineServerCount(): Int = Pool.filter { !it.isRunning() }.size
    internal fun getAvailableTypes() = ServerTypePool
    internal fun scanServer(){
        val gson = GsonBuilder().registerTypeAdapter(MCJEServer::class.java, MCJEServerAdapter).create()
        Configuration.getServerFolder().listFiles().forEach fl@{ f ->
            if(f.isDirectory){
                log.info("Searching Directory >> $f")
                val target = f.listFiles().find { sf -> sf.name == "MK-ServerLauncher.json" }
                if(target == null){
                    log.warn("Skipped")
                }else{
                    log.info("Introspecting Server Description >> $f")
                    Pool.add(gson.fromJson(FileReader(target, StandardCharsets.UTF_8), object: TypeToken<MCJEServer>(){}))
                }
            }
        }
    }

    internal fun saveServers(){ Pool.forEach { it.saveToFile() } }
    internal fun getType(id: String): ServerType = ServerTypePool.find { it.id == id } ?: ServerType.UNKNOWN
    internal fun addType(type: ServerType){
        if (ServerTypePool.contains(type)){
            log.warn("Ambiguous Server Type Detected >> ${type.id}")
        }
    }
}


