package me.mucloud.application.mk.serverlauncher.common.server.mcserver

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import me.mucloud.application.mk.serverlauncher.common.env.EnvPool
import me.mucloud.application.mk.serverlauncher.common.env.JavaEnvironment
import me.mucloud.application.mk.serverlauncher.common.manage.ConfigurationFactory
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool

/**
 * MC Java Edition Server.
 *
 * MuPack Internal Server Template
 *
 * @since DEV.1
 * @author Mu_Cloud
 */
data class MCJEServer(
    private var name: String,
    private val version: String,
    private var type: ServerType,
    private var desc: String,
    private var port: Int = 25565,
    private var env: JavaEnvironment,
    @SerializedName("before_works") private var beforeWork: MutableList<String> = mutableListOf(),
){
    private var location: File = File(ConfigurationFactory.getConfiguration().getServerFolder(), name)
    private var running: Boolean = false
    private val totalFailCount: Int = 0
    private val totalPassCount: Int = 0
    private val config: Config = Config()

    private val serverFlow = MutableSharedFlow<JsonObject>()

    init{
        if(!location.exists()){
            location.mkdirs()
        }
    }

    fun deploy(){

    }

    fun regBeforeWork(work: String){
        beforeWork.add(work)
    }

    fun regBeforeWork(vararg work: String){
        beforeWork.addAll(work)
    }

    fun runBeforeWorks(): Int{
        beforeWork.forEach { be ->
            try {
                Runtime.getRuntime().exec(be).errorStream.bufferedReader().forEachLine { l ->
                    runBlocking { serverFlow.emit(JsonObject().also { j ->
                        j.addProperty("type", "console.out:before_work")
                        j.add("data", JsonObject().also { data ->
                            data.addProperty("index", beforeWork.indexOf(be))
                            data.addProperty("msg", l)
                        })
                    })}
                }
            }catch (e: Exception) {
                // TODO(ERR PACK)
                return 1
            }
        }
        return 0
    }

    fun start() {
        runBeforeWorks()
        //TODO("Launch MC SERVER Command")
        running = true
    }

    fun stop() {
        running = false
    }

    fun getName(): String = name
    fun setName(name: String) { this.name = name }

    fun isRunning(): Boolean = running

    fun totalFailCount(): Int = totalFailCount

    fun totalPassCount(): Int = totalPassCount

    fun getDescription(): String = desc
    fun setDescription(desc: String){ this.desc = desc }

    fun getFolder(): File = location
    fun setFolder(loc: File){
        if(loc.exists() && loc.isDirectory){
            location = loc
        }else{
            throw RuntimeException("Server Folder Not Found")
        }
    }

    fun getPort(): Int = port
    fun setPort(port: Int) { this.port = port }

    fun getVersion(): String = version

    fun getType(): ServerType = type

    fun getEnv(): JavaEnvironment = env
    fun setEnv(env: JavaEnvironment) { this.env = env }

    fun lastLaunchTime(): LocalDateTime = LocalDateTime.now() //todo

    fun getConfig(): Config = config
    fun getBeforeWorks(): List<String> = beforeWork

    fun saveToFile(){
        FileWriter(File(getFolder(), "MK-ServerLauncher.json").also { if(!it.exists()) it.createNewFile() }, StandardCharsets.UTF_8).also {
            it.write(GsonBuilder().setPrettyPrinting().create().toJson(this))
            it.flush()
        }
    }

    fun getServerFlow() = serverFlow

    fun sendCommand(cmd: String){
        TODO("Implementation in iLoveMu")
    }

    fun sendMsg(msg: String){
        TODO("Implementation in iLoveMu")
    }

    inner class Config{
        val configFile: File = File(getFolder(), "server.properties")
        val anotherConfig = mutableMapOf<String, Any>()
        val properties: Properties = Properties().apply {
            load(FileReader(configFile))
        }

        fun get(key: String): Any = properties.getProperty(key)

        fun set(key: String, newValue: Any){
            properties.setProperty(key, newValue.toString())
        }

        fun del(key: String){
            anotherConfig.remove(key)
        }
    }
}

object MCJEServerAdapter: JsonSerializer<MCJEServer>, JsonDeserializer<MCJEServer>{
    override fun serialize(
        s: MCJEServer,
        t: Type,
        c: JsonSerializationContext
    ): JsonElement {
        return JsonObject().apply {
            addProperty("name", s.getName())
            addProperty("desc", s.getDescription())
            addProperty("version", s.getVersion())
            addProperty("type", s.getType().id)
            addProperty("port", s.getPort())
            addProperty("env", s.getEnv().name)
            add("before_works", c.serialize(s.getBeforeWorks()))
            addProperty("location", s.getFolder().absolutePath)
        }
    }

    override fun deserialize(
        j: JsonElement,
        t: Type,
        c: JsonDeserializationContext
    ): MCJEServer {
        j.asJsonObject.also { v ->
            return MCJEServer(
                v["name"].asString,
                v["version"].asString,
                ServerPool.getType(v["type"].asString),
                v["desc"].asString,
                v["port"].asInt,
                EnvPool.getEnv(v["env"].asString)!!,
                c.deserialize(v["before_works"], MutableList::class.java),
            ).apply { setFolder(File(v["location"].asString)) }
        }
    }
}