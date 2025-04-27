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
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.put
import me.mucloud.application.MK.ServerLauncher.internal.env.EnvPool
import me.mucloud.application.MK.ServerLauncher.internal.server.ServerPool

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
            /**
             *
             *  Serialized JSON Message for CreateServer
             *
             *  Response a [HttpStatusCode]
             *
             *  while the status code is:
             *
             *  [HttpStatusCode.OK] - Success in this step.
             *  [HttpStatusCode.BadRequest] - Wrong
             *
             */
            post("server/create/{name}/{step}") {
                val name = call.parameters["name"]
                val step = call.parameters["step"]

                if (name != null && step != null) {
                    when (step.toInt()) {
                        0 -> {
                            if(ServerPool.getServer(name) == null){
                                call.respond(HttpStatusCode.OK, "OK! MuCore Passed Step $step.")
                            } else {
                                call.respond(
                                    HttpStatusCode.BadRequest,
                                    "MuCore detected server already exist using this Name, try to refresh MuView?"
                                )
                            }
                        }
                        1 -> {

                        }

                        2 -> {

                        }
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            post("env/create"){
                val rawData = call.receive<JsonObject>()
                val envName = rawData["envName"]?.asString ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing property \"Name\"")
                val envPath = rawData["envPath"]?.asString ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing property \"Path\"")
                if(EnvPool.addEnv(envName, envPath)) {
                    call.respond(HttpStatusCode.OK)
                }else{
                    call.respond(HttpStatusCode.BadRequest, "MuEnvironment already exists.")
                }
            }

            post("env/delete/{index}"){
                try{
                    val index = call.parameters["index"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing property \"Index\"")
                    if(EnvPool.getEnvList().size > index.toInt()) {
                        EnvPool.deleteEnv(index.toInt())
                        call.respond(HttpStatusCode.OK, "Delete Success.")
                    }else{
                        call.respond(HttpStatusCode.BadRequest, "MuCore received an invalid index.")
                    }
                }catch(e: NumberFormatException){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            get("servers") {
                call.respond(ServerPool.getServerList())
            }

            /**
             *
             *  Serialized JSON Message for Environment List.
             *
             *  Send a Json Array contains some Json Objects like following:
             *
             *      [
             *          {
             *              "env_name": "",
             *              "env_version": "",
             *              "env_path": ""
             *          }
             *          ...
             *      ]
             *
             *  @author Mu_Cloud
             *  @since DEV 1
             */
            get("envs") {
                call.respondText(
                    contentType = ContentType.Application.Json,
                    status = HttpStatusCode.OK,
                    text = buildJsonArray {
                        EnvPool.getEnvList().forEach { i ->
                            addJsonObject {
                                put("env_name", i.getEnvName())
                                put("env_version", i.getEnvVersion())
                                put("env_path", i.getLocation())
                            }
                        }
                    }.toString()
                )
            }
        }
    }
}