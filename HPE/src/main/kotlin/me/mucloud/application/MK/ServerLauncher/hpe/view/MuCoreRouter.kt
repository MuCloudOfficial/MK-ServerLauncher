package me.mucloud.application.mk.serverlauncher.hpe.view

import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.initMuCoreRoute(){
    routing {
        route("api/v1/mucore"){

        }
    }
}