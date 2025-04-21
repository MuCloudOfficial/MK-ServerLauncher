package me.mucloud.application.MK.ServerLauncher.view

import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.*
import me.mucloud.application.MK.ServerLauncher.internal.env.EnvPool
import me.mucloud.application.MK.ServerLauncher.internal.server.ServerPool

fun Application.initRoute() {
    //TODO("Remember Delete!!! Dev Use!!!)
    //Allow Any host & method from Cross origin
    install(CORS){
        anyHost()
        anyMethod()
    }
    install(ContentNegotiation) {
      gson {
          setPrettyPrinting()
      }
    }
    // Validate Requests
    install(RequestValidation) {
        validate<JsonElement> { j ->
            if(j.jsonObject["token"] == null){
                ValidationResult.Invalid("Not Define Token in Request Body!")
            }else if(j.jsonObject["token"].toString() != "lovemumu"){
                ValidationResult.Invalid("Error Token!")
            }else{
                ValidationResult.Valid
            }
        }
    }
    routing {
        /**
         *
         *  Serialized JSON Message for CreateServer
         *
         *  Response a [HttpStatusCode]
         *
         *  while the status code is:
         *
         *  [HttpStatusCode.OK] - Success in this step.
         *  other - Wrong
         *
         */
        post("/api/v1/server/create/{name}/{step}") {
            val name = call.parameters["name"]
            val step = call.parameters["step"]

            if (name != null && step != null) {
                when (step.toInt()) {
                    0 -> {
                        if (ServerPool.getServer(name) == null) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.BadRequest)
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

        get("/api/v1/servers") {
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
        get("/api/v1/envs") {
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