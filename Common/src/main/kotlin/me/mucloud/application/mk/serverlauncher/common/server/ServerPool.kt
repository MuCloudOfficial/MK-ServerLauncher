package me.mucloud.application.mk.serverlauncher.common.server

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.nio.charset.StandardCharsets
import me.mucloud.application.mk.serverlauncher.common.manage.ConfigurationFactory
import me.mucloud.application.mk.serverlauncher.common.external.log
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.MCJEServer
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.MCJEServerAdapter
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.MCJEServerType

object ServerPool {

    private val ServerTypePool = mutableListOf<MCJEServerType>()

    private val Pool: MutableList<MCJEServer> = mutableListOf()

    fun addServer(server: MCJEServer){ Pool.add(server); server.saveToFile() }
    fun MCJEServer.delete(){ getFolder().deleteRecursively(); Pool.remove(this) }
    fun MCJEServer.remove(){ File(getFolder(), "MK-ServerLauncher.json").delete(); Pool.remove(this) }
    fun getServer(name: String): MCJEServer? = Pool.find { name == it.getName() }
    fun getServerList(): List<MCJEServer> = Pool
    fun getTotalServer(): Int = Pool.size
    fun getOnlineServerCount(): Int = Pool.filter { it.isRunning() }.size
    fun getOfflineServerCount(): Int = Pool.filter { !it.isRunning() }.size
    fun getAvailableTypes() = ServerTypePool
    fun scanServer(){
        val gson = GsonBuilder().registerTypeAdapter(MCJEServer::class.java, MCJEServerAdapter).create()
        ConfigurationFactory.getConfiguration().getServerFolder().listFiles().forEach fl@{ f ->
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

    fun saveServers(){ Pool.forEach { it.saveToFile() } }
    fun getType(id: String): MCJEServerType = ServerTypePool.find { it.id == id } ?: MCJEServerType.UNKNOWN
    internal fun addType(type: MCJEServerType){
        if (ServerTypePool.contains(type)){
            log.warn("Ambiguous Server Type Detected >> ${type.id}")
        }
    }
}


