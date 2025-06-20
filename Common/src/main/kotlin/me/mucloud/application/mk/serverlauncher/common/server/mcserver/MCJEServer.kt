package me.mucloud.application.mk.serverlauncher.common.server.mcserver

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import me.mucloud.application.mk.serverlauncher.common.env.EnvPool
import me.mucloud.application.mk.serverlauncher.common.env.JavaEnvironment
import me.mucloud.application.mk.serverlauncher.common.manage.Configuration
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.MCJEServer.Config
import java.io.File
import java.io.FileWriter
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

/**
 * MC Java Edition Server.
 *
 * MuPack Internal Server Template
 *
 * @since DEV.1
 * @author Mu_Cloud
 */
@Serializable(MCJEServerSerializer::class)
data class MCJEServer(
    private var name: String,
    private val version: String,
    private var type: ServerType,
    private var desc: String,
    private var port: Int = 25565,
    private var env: JavaEnvironment,
    private var config: Config,
    @SerializedName("before_works") private var beforeWork: MutableList<String> = mutableListOf(),
){
    private var location: File = File(Configuration.getServerFolder(), name)
    private var running: Boolean = false
    private val totalFailCount: Int = 0
    private val totalPassCount: Int = 0

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

    fun start() {
        beforeWork.forEach { be ->
            Runtime.getRuntime().exec(be).errorStream.bufferedReader().forEachLine { l ->
                runBlocking { serverFlow.emit(JsonObject().also { j ->
                    j.addProperty("type", "console.out:before_work")
                    j.add("data", JsonObject().also { data ->
                        data.addProperty("index", beforeWork.indexOf(be))
                        data.addProperty("msg", l)
                    })
                }) }
            }
        }
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
            it.write(Json{ prettyPrint = true; encodeDefaults = true }.encodeToString(this))
            it.flush()
        }
    }

    fun getServerFlow() = serverFlow

    fun sendCommand(cmd: String){
        TODO()
    }

    fun sendMsg(msg: String){
        TODO()
    }

    @Serializable
    data class Config(
        internal var isOnline: Boolean = true,
        internal var isWhileListed: Boolean = false,
        internal var maxPlayer: Int = 20,
        internal var viewDistance: Int = 10,
        internal var allowNether: Boolean = true,
        internal var spawnProtectRange: Int = 10,
        internal var jvmFlagTemplate: String = "none",
        internal var anotherJVMFlags: String = "",
        internal var allowGUI: Boolean = false,
        internal var minimumAllocatedMemory: Int = 512,
        internal var maximumAllocatedMemory: Int = 512,
        internal var anotherConfig: MutableMap<String, String> = mutableMapOf()
    ){

        fun add(key: String, value: String){
            anotherConfig.put(key, value)
        }

        fun set(key: String, newValue: String){

        }

        fun del(key: String){
            anotherConfig.remove(key)
        }
    }

}

object MCJEServerSerializer: KSerializer<MCJEServer> {
    @OptIn(ExperimentalSerializationApi::class)
    @Transient override val descriptor: SerialDescriptor = buildClassSerialDescriptor("mupack.server.mcjeserver"){
        element<String>("name")
        element<String>("version")
        element<String>("type")
        element<String>("desc")
        element<Int>("port")
        element<JavaEnvironment>("env")
        element<Config>("config")
        element("before_works", listSerialDescriptor<String>())
        element<String>("location")
    }

    @OptIn(InternalSerializationApi::class)
    override fun deserialize(decoder: Decoder): MCJEServer = decoder.decodeStructure(descriptor){
        var name: String? = null
        var version: String? = null
        var type: ServerType? = null
        var desc: String? = null
        var port: Int? = null
        var env: JavaEnvironment? = null
        var config: Config? = null
        var beforeWork: MutableList<String>? = null
        var location: File? = null

        l@ while (true){
            when(val i = decodeElementIndex(descriptor)){
                DECODE_DONE -> break@l
                0 -> { name = decodeStringElement(descriptor, 0) }
                1 -> { version = decodeStringElement(descriptor, 1) }
                2 -> { type = ServerPool.getType(decodeStringElement(descriptor, 2)) }
                3 -> { desc = decodeStringElement(descriptor, 3) }
                4 -> { port = decodeIntElement(descriptor, 4) }
                5 -> { env = decodeSerializableElement(descriptor, 5, serializer<JavaEnvironment>()) }
                6 -> { config = decodeSerializableElement(descriptor, 6, serializer<Config>()) }
                7 -> { beforeWork = decodeSerializableElement(descriptor, 7, serializer<List<String>>()).toMutableList() }
                8 -> { location = File(decodeStringElement(descriptor, 8)) }
                else -> throw SerializationException("Invalid Index $i")
            }
        }
        if(name == null || version == null || type == null || desc == null || port == null || env == null || config == null || beforeWork == null || location == null) throw SerializationException("Invalid Field, please re-check")
        return@decodeStructure MCJEServer(name, version, type, desc, port, env, config, beforeWork).also { it.setFolder(location) }
    }

    override fun serialize(encoder: Encoder, value: MCJEServer) = encoder.encodeStructure(descriptor){
        encodeStringElement(descriptor, 0, value.getName())
        encodeStringElement(descriptor, 1, value.getVersion())
        encodeStringElement(descriptor, 2, value.getType().id)
        encodeStringElement(descriptor, 3, value.getDescription())
        encodeIntElement(descriptor, 4, value.getPort())
        encodeSerializableElement(descriptor, 5, serializer<JavaEnvironment>(), value.getEnv())
        encodeSerializableElement(descriptor, 6, serializer<Config>(), value.getConfig())
        encodeSerializableElement(descriptor, 7, serializer<List<String>>(), value.getBeforeWorks())
        encodeStringElement(descriptor, 8, value.getFolder().absolutePath.toString())
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
            add("config", c.serialize(s.getConfig()))
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
                c.deserialize(v["config"], Config::class.java),
                c.deserialize(v["before_works"], MutableList::class.java),
            ).apply { setFolder(File(v["location"].asString)) }
        }
    }
}