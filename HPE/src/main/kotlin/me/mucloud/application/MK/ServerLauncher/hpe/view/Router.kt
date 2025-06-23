package me.mucloud.application.mk.serverlauncher.hpe.view

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.io.File
import java.util.*
import java.util.jar.JarFile
import me.mucloud.application.mk.serverlauncher.common.env.EnvPool
import me.mucloud.application.mk.serverlauncher.common.packets.MuPacket
import me.mucloud.application.mk.serverlauncher.common.packets.MuPacketFactory
import me.mucloud.application.mk.serverlauncher.common.packets.MuSend2ServerPacket
import me.mucloud.application.mk.serverlauncher.common.packets.MuServerConfigPacket
import me.mucloud.application.mk.serverlauncher.common.packets.MuServerInfoPacket
import me.mucloud.application.mk.serverlauncher.common.packets.MuServerStartPacket
import me.mucloud.application.mk.serverlauncher.common.packets.MuServerStopPacket
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool.delete
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool.remove
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.MCJEServer
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.MCJEServerAdapter
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.ServerType
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.ServerTypeSerializer

fun Application.initRoute() {
    //TODO("Remember Delete!!! Dev Use!!!)
    //Allow Any host & method from Cross origin
    install(CORS){
        anyHost()
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Options)
        allowSameOrigin = true
        allowHeaders {
            it.equals("Content-Type", true)
        }
        allowNonSimpleContentTypes = true
    }
    install(ContentNegotiation) {
        gson{
//            val factory =
//                RuntimeTypeAdapterFactory
//                    .of(MuPacket::class.java, "type")
//                    .registerSubtype(MuServerConfigPacket::class.java, "config")
//                    .registerSubtype(MuSend2ServerPacket::class.java, "msg.in:send")
//                    .registerSubtype(MuBroadcastPacket::class.java, "msg.in:broadcast")
//                    .registerSubtype(MuServerInfoPacket::class.java, "server:info")
//                    .registerSubtype(MuServerStartPacket::class.java, "server:start")
//                    .registerSubtype(MuServerStopPacket::class.java, "server:stop")

            setPrettyPrinting()
            registerTypeAdapter(MCJEServer::class.java, MCJEServerAdapter)
            registerTypeAdapter(ServerType::class.java, ServerTypeSerializer)

            registerTypeAdapter(MuPacket::class.java, MuPacketFactory)
            registerTypeAdapter(MuServerInfoPacket::class.java, MuPacketFactory)
            registerTypeAdapter(MuServerStartPacket::class.java, MuPacketFactory)
            registerTypeAdapter(MuServerStopPacket::class.java, MuPacketFactory)
            registerTypeAdapter(MuSend2ServerPacket::class.java, MuPacketFactory)
            registerTypeAdapter(MuServerConfigPacket::class.java, MuPacketFactory)
//            registerTypeAdapterFactory(factory)
        }
    }
    routing {
        route("api/v1"){
            route("test"){
                get("MuPacketList"){
                    val testServer = MCJEServer(
                        "testserver",
                        "1.19.2",
                        ServerType.PAPER,
                        "",
                        25565,
                        EnvPool.getEnv("TestEnv")!!,
                        MCJEServer.Config()
                    )
                    call.respond(listOf(MuServerInfoPacket(testServer),
                        MuServerStartPacket(testServer),
                        MuServerStopPacket(testServer),
                        MuSend2ServerPacket(testServer, "TestPlayer", "MSG"),
                        MuServerConfigPacket(MuServerConfigPacket.ConfigOperationType.ADD, "test", "test"))
                    )
                }
            }
            route("server"){
                get("availableType"){
                    call.respond(ServerPool.getAvailableTypes())
                }
                get("list") {
                    call.respond(ServerPool.getServerList())
                }
                get("delete/{name}"){
                    (ServerPool.getServer(
                        call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    ) ?: return@get call.respond(HttpStatusCode.BadRequest)).delete()
                    call.respond(HttpStatusCode.OK)
                }
                get("remove/{name}"){
                    (ServerPool.getServer(
                        call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    ) ?: return@get call.respond(HttpStatusCode.BadRequest)).remove()
                    call.respond(HttpStatusCode.OK)
                }
                get("start/{name}"){
                    (ServerPool.getServer(
                        call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    ) ?: return@get call.respond(HttpStatusCode.BadRequest)).start()
                    call.respond(HttpStatusCode.OK)
                }
                get("stop/{name}"){
                    (ServerPool.getServer(
                        call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    ) ?: return@get call.respond(HttpStatusCode.BadRequest)).stop()
                    call.respond(HttpStatusCode.OK)
                }

                post("create") {
                    call.receive<JsonObject>().also { j ->
                        if(ServerPool.getServer(j["name"].asString) != null){
                            call.respond(HttpStatusCode.BadRequest)
                        }else{
                            try {
                                ServerPool.addServer(
                                    MCJEServer(
                                        j["name"].asString,
                                        j["version"].asString,
                                        ServerPool.getType(j["type"].asString),
                                        j["desc"].asString,
                                        j["port"].asInt,
                                        EnvPool.getEnv(j["env"].asString)!!,
                                        MCJEServer.Config(
                                            jvmFlagTemplate = j["jvm_flag_template"].asString,
                                            anotherJVMFlags = j["jvm_aflags"].asString,
                                            maxPlayer = j["max_player"].asInt,
                                            minimumAllocatedMemory = j["minimum_mem"].asInt,
                                            maximumAllocatedMemory = j["maximum_mem"].asInt,
                                            isOnline = j["online"].asBoolean,
                                            isWhileListed = j["whitelist"].asBoolean,
                                            spawnProtectRange = j["spawn_protect"].asInt,
                                            viewDistance = j["view_distance"].asInt,
                                            allowGUI = j["allow_gui"].asBoolean,
                                            allowNether = j["allow_nether"].asBoolean,
                                        ),
                                        j["before_works"].asJsonArray.map { i -> return@map (i.asJsonObject)["value"].asString }.toMutableList()
                                    )
                                )
                            }catch (e: Exception) {
                                call.respond(HttpStatusCode.BadRequest, e.toString())
                                e.printStackTrace()
                            }
                            call.respond(HttpStatusCode.OK)
                        }
                    }
                }

                post("import"){
                    call.receive<JsonObject>().also { r ->
                        try{
                            val target = r["name"].asString
                            val targetPath = r["path"].asString

                            var type = "Unknown"
                            var version = "Unknown"
                            if(ServerPool.getServer(target) != null || !File(targetPath).exists()){
                                call.respond(HttpStatusCode.BadRequest)
                            }else{
                                val targetJar = JarFile(File(targetPath))
                                type = targetJar.manifest.mainAttributes["Main-Class"].toString().let {
                                    if(it.contains("papermc")){
                                        "paper"
                                    }else if(it.contains("leavesmc")){
                                        "leaves"
                                    }else{
                                        "Unknown"
                                    }
                                }
                                var versionFile = targetJar.getJarEntry("version.json")
                                if(versionFile != null){
                                    JsonParser.parseReader(targetJar.getInputStream(versionFile).bufferedReader()).asJsonObject.also { version = it["id"].asString }
                                }else{
                                    versionFile = targetJar.getJarEntry("patch.properties")
                                    if(versionFile != null){
                                        Properties().also{
                                            it.load(targetJar.getInputStream(versionFile))
                                            version = it.getProperty("version")
                                        }
                                    }
                                }
                            }

                            val targetFolder = File(targetPath).parentFile
                            val targetConf = File(targetFolder, "server.properties")
                            val config = MCJEServer.Config()
                            if(targetConf.exists()){
                                Properties().also {
                                    it.load(targetConf.reader())
                                    config.allowNether = it.getProperty("allow-nether").toBoolean()
                                    config.isWhileListed = it.getProperty("white-list").toBoolean()
                                    config.isOnline = it.getProperty("online-mode").toBoolean()
                                    config.maxPlayer = it.getProperty("max-players").toInt()
                                    config.viewDistance = it.getProperty("view-distance").toInt()
                                    config.spawnProtectRange = it.getProperty("spawn-protection").toInt()
                                }
                            }
                            ServerPool.addServer(MCJEServer(
                                target, version, ServerPool.getType(type),
                                r["desc"].asString,
                                r["port"].asInt,
                                EnvPool.getEnv(r["env"].asString)!!,
                                config,
                                r["before_works"].asJsonArray.map { i -> return@map (i.asJsonObject)["value"].asString }.toMutableList()
                            ).also {
                                it.setFolder(targetFolder)
                                it.saveToFile()
                            })
                            call.respond(HttpStatusCode.OK)
                        }catch (e: Exception){
                            call.respond(HttpStatusCode.BadRequest, e.toString())
                            e.printStackTrace()
                        }
                    }
                }
            }

            route("env"){
                get("list") {
                    call.respond(EnvPool.getEnvList())
                }

                post("create"){
                    val rawData = call.receive<JsonObject>()
                    val envName = rawData["name"]?.asString ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing property \"Name\"")
                    val envPath = rawData["path"]?.asString ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing property \"Path\"")
                    if(EnvPool.addEnv(envName, envPath)) {
                        call.respond(HttpStatusCode.OK)
                    }else{
                        call.respond(HttpStatusCode.BadRequest, "MuEnvironment already exists.")
                    }
                }

                get("delete/{name}"){
                    try{
                        if (EnvPool.deleteEnv(call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid Environment Name."))) call.respond(HttpStatusCode.OK, "Delete Success.") else call.respond(HttpStatusCode.InternalServerError, "MuCore Not have this Environment, please Re-Get ENVLIST.")
                    }catch(e: NumberFormatException){
                        call.respond(HttpStatusCode.BadRequest, e.toString())
                    }
                }
            }
        }
    }
}