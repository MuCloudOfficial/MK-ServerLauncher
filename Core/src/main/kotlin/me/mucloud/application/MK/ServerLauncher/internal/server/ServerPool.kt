package me.mucloud.application.MK.ServerLauncher.internal.server

import com.google.gson.Gson
import kotlinx.serialization.Serializable
import me.mucloud.application.MK.ServerLauncher.internal.manage.Configuration
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.MCJEServer
import me.mucloud.application.MK.ServerLauncher.log
import java.io.File
import java.io.FileReader

object ServerPool {

    private val AvailableTypePool = mutableListOf(
        AvailableType("spigot", "Spigot", "Minecraft Dedicated Server", "Unknown"),
        AvailableType("paper", "Paper & PaperSpigot", "High Performance Server core based on Spigot", "https://api.papermc.io/v2/projects/paper"),
        AvailableType("leaves", "Leaves", "Fixed some broken Features based on Paper", "https://api.leavesmc.org/v2/projects/leaves")
    )

    private val Pool: MutableList<MCJEServer> = mutableListOf()

    internal fun addServer(server: MCJEServer){ Pool.add(server); server.saveToFile() }
    internal fun MCJEServer.delete(){ getFolder().deleteRecursively(); Pool.remove(this) }
    internal fun MCJEServer.remove(){ File(getFolder(), "MK-ServerLauncher.json").delete(); Pool.remove(this) }
    internal fun getServer(name: String): MCJEServer? = Pool.find { name == it.getName() }
    internal fun getServerList(): List<MCJEServer> = Pool
    internal fun getTotalServer(): Int = Pool.size
    internal fun getOnlineServerCount(): Int = Pool.filter { it.isRunning() }.size
    internal fun getOfflineServerCount(): Int = Pool.filter { !it.isRunning() }.size
    internal fun getAvailableType() = AvailableTypePool
    internal fun scanServer(){
        Configuration.getServerFolder().listFiles().forEach fl@{ f ->
            if(f.isDirectory){
                log.info("Searching Directory >> $f")
                val target = f.listFiles().find { sf -> sf.name == "MK-ServerLauncher.json" }
                if(target == null){
                    log.warn("Skipped")
                }else{
                    log.info("Introspecting Server Description >> $f")
                    addServer(Gson().fromJson(FileReader(target).readText(), MCJEServer::class.java))
                }
            }
        }
    }

    internal fun saveServers(){ Pool.forEach { it.saveToFile() } }
    internal fun getType(id: String): AvailableType = AvailableTypePool.find { it.id == id }!!

}

@Serializable data class AvailableType(
    val id: String,
    val name: String,
    val desc: String,
    val api: String,
)


