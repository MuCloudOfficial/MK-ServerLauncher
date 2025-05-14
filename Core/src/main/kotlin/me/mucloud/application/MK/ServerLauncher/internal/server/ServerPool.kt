package me.mucloud.application.MK.ServerLauncher.internal.server

import com.google.gson.Gson
import me.mucloud.application.MK.ServerLauncher.internal.env.JavaEnvironment
import me.mucloud.application.MK.ServerLauncher.internal.manage.Configuration
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.MCJEServer
import java.io.FileReader
import java.nio.charset.StandardCharsets

object ServerPool {

    private val AvailableType = mutableMapOf(
        "paper" to "Paper & PaperSpigot",
        "folia" to "Folia"
    )

    private val Pool: MutableList<MCJEServer> = mutableListOf(
//        MCJEServer("TestServer", "1.19.2", "paper", "", 25566, JavaEnvironment("", ""), MCJEServer.Config())
    )

    internal fun addServer(server: MCJEServer){ Pool +server }
    internal fun delServer(server: MCJEServer){ Pool -server }
    internal fun getServer(name: String): MCJEServer? = Pool.find { name == it.getName() }
    internal fun getServerList(): List<MCJEServer> = Pool
    internal fun getTotalServer(): Int = Pool.size
    internal fun getOnlineServerCount(): Int = Pool.filter { it.isRunning() }.size
    internal fun getOfflineServerCount(): Int = Pool.filter { !it.isRunning() }.size
    internal fun getAvailableType() = AvailableType
    internal fun scanServer(){
        Configuration.getServerFolder().listFiles().forEach fl@{ f ->
            if(f.isDirectory) addServer(Gson().fromJson(FileReader(f.listFiles().find { sf -> sf.name == "MK-ServerLauncher.json" } ?: return@fl, StandardCharsets.UTF_8), MCJEServer::class.java))
        }
    }

}


