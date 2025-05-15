package me.mucloud.application.MK.ServerLauncher.view

import com.google.gson.JsonObject
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.mucloud.application.MK.ServerLauncher.internal.env.EnvPool
import me.mucloud.application.MK.ServerLauncher.internal.server.ServerPool
import me.mucloud.application.MK.ServerLauncher.internal.server.ServerPool.delete
import me.mucloud.application.MK.ServerLauncher.internal.server.ServerPool.remove
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.MCJEServer

fun Application.initRoute() {
    //TODO("Remember Delete!!! Dev Use!!!)
    //Allow Any host & method from Cross origin
    install(CORS) {
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
        gson {
            setPrettyPrinting()
        }
    }
    routing {
        route("api/v1"){
            route("server"){
                get("availableType"){
                    call.respond(ServerPool.getAvailableType())
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

                /**
                 *
                 *  Serialized JSON Message for CreateServer
                 *
                 *  Response a [HttpStatusCode]
                 *
                 *  while the status code is:
                 *
                 *  [HttpStatusCode.OK] - Success in Create Server.
                 *  [HttpStatusCode.BadRequest] - Wrong
                 *
                 *  @since DEV.20
                 *  @author Mu_Cloud
                 *
                 */
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
                                        j["type"].asString,
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
                                        )
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
            }

            route("env"){
                /**
                 *  Serialized JSON Message for Environment List.
                 *  Send a Json Array contains some Json Objects like following:
                 *
                 *     [
                 *         {
                 *             "env_name": "",
                 *             "env_version": "",
                 *             "env_path": ""
                 *         }
                 *         ...
                 *     ]
                 *
                 *  @since DEV.20
                 *  @author Mu_Cloud
                 */
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