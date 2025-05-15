package me.mucloud.application.MK.ServerLauncher.internal.server

import com.google.gson.Gson
import me.mucloud.application.MK.ServerLauncher.log
import me.mucloud.application.MK.ServerLauncher.internal.manage.Configuration
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.MCJEServer
import java.io.File
import java.io.FileReader

object ServerPool {

    private val AvailableType = mutableMapOf(
        "paper" to "Paper & PaperSpigot",
        "folia" to "Folia"
    )

    private val Pool: MutableList<MCJEServer> = mutableListOf(
//        MCJEServer("TestServer", "1.19.2", "paper", "", 25566, JavaEnvironment("", ""), MCJEServer.Config())
    )

    internal fun addServer(server: MCJEServer){ Pool.add(server); server.saveToFile() }
    internal fun MCJEServer.delete(){ getFolder().deleteRecursively(); Pool.remove(this) }
    internal fun MCJEServer.remove(){ File(getFolder(), "MK-ServerLauncher.json").delete(); Pool.remove(this) }
    internal fun getServer(name: String): MCJEServer? = Pool.find { name == it.getName() }
    internal fun getServerList(): List<MCJEServer> = Pool
    internal fun getTotalServer(): Int = Pool.size
    internal fun getOnlineServerCount(): Int = Pool.filter { it.isRunning() }.size
    internal fun getOfflineServerCount(): Int = Pool.filter { !it.isRunning() }.size
    internal fun getAvailableType() = AvailableType
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

    internal fun saveServers(){
        Pool.forEach { it.saveToFile() }
    }

}


