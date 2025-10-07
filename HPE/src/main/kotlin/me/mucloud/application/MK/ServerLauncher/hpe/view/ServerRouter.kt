package me.mucloud.application.mk.serverlauncher.hpe.view

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.io.File
import java.util.Properties
import java.util.jar.JarFile
import me.mucloud.application.mk.serverlauncher.common.env.EnvPool
import me.mucloud.application.mk.serverlauncher.common.env.JavaEnvironment
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool.delete
import me.mucloud.application.mk.serverlauncher.common.server.ServerPool.remove
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.MCJEServer

fun Application.initServerRoute() {
    routing {
        route("api/v1/server") {
            // RESTful API | Servers
            get("availableType") {
                call.respond(ServerPool.getAvailableTypes())
            }
            get("list") {
                call.respond(ServerPool.getServerList())
            }
            get("delete/{name}") {
                (ServerPool.getServer(
                    call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                ) ?: return@get call.respond(HttpStatusCode.BadRequest)).delete()
                call.respond(HttpStatusCode.OK)
            }
            get("remove/{name}") {
                (ServerPool.getServer(
                    call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                ) ?: return@get call.respond(HttpStatusCode.BadRequest)).remove()
                call.respond(HttpStatusCode.OK)
            }
            get("start/{name}") {
                (ServerPool.getServer(
                    call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                ) ?: return@get call.respond(HttpStatusCode.BadRequest)).start()
                call.respond(HttpStatusCode.OK)
            }
            get("stop/{name}") {
                (ServerPool.getServer(
                    call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                ) ?: return@get call.respond(HttpStatusCode.BadRequest)).stop()
                call.respond(HttpStatusCode.OK)
            }

            post("create") {
                call.receive<JsonObject>().also { j ->
                    if (ServerPool.getServer(j["name"].asString) != null) {
                        call.respond(HttpStatusCode.BadRequest)
                    } else {
                        try {
                            ServerPool.addServer(
                                MCJEServer(
                                    j["name"].asString,
                                    j["version"].asString,
                                    ServerPool.getType(j["type"].asString),
                                    j["desc"].asString,
                                    j["port"].asInt,
                                    EnvPool.getEnv(j["env"].asString)!! as JavaEnvironment,
                                    j["before_works"].asJsonArray.map { i -> return@map (i.asJsonObject)["value"].asString }
                                        .toMutableList()
                                )
                            )
                        } catch (e: Exception) {
                            call.respond(HttpStatusCode.BadRequest, e.toString())
                            e.printStackTrace()
                        }
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }

            post("import") {
                call.receive<JsonObject>().also { r ->
                    try {
                        val target = r["name"].asString
                        val targetPath = r["path"].asString

                        var type = "Unknown"
                        var version = "Unknown"
                        val targetServer = ServerPool.getServer(target)
                        if (targetServer != null || !File(targetPath).exists()) {
                            call.respond(HttpStatusCode.BadRequest)
                        } else {
                            val targetJar = JarFile(File(targetPath))
                            type = targetJar.manifest.mainAttributes["Main-Class"].toString().let {
                                if (it.contains("papermc")) {
                                    "paper"
                                } else if (it.contains("leavesmc")) {
                                    "leaves"
                                } else {
                                    "Unknown"
                                }
                            }
                            var versionFile = targetJar.getJarEntry("version.json")
                            if (versionFile != null) {
                                JsonParser.parseReader(
                                    targetJar.getInputStream(versionFile).bufferedReader()
                                ).asJsonObject.also { version = it["id"].asString }
                            } else {
                                versionFile = targetJar.getJarEntry("patch.properties")
                                if (versionFile != null) {
                                    Properties().also {
                                        it.load(targetJar.getInputStream(versionFile))
                                        version = it.getProperty("version")
                                    }
                                }
                            }
                        }

                        val targetFolder = File(targetPath).parentFile
                        ServerPool.addServer(
                            MCJEServer(
                                target, version, ServerPool.getType(type),
                                r["desc"].asString,
                                r["port"].asInt,
                                EnvPool.getEnv(r["env"].asString)!! as JavaEnvironment,
                                r["before_works"].asJsonArray.map { i -> return@map (i.asJsonObject)["value"].asString }
                                    .toMutableList()
                            ).apply {
                                setFolder(targetFolder)
                                saveToFile()
                            })
                        call.respond(HttpStatusCode.OK)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, e.toString())
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}