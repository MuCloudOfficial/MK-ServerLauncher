package me.mucloud.application.mk.serverlauncher.muview

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import me.mucloud.application.mk.serverlauncher.MuCoreMini
import me.mucloud.application.mk.serverlauncher.muenv.JavaEnvironment
import me.mucloud.application.mk.serverlauncher.muenv.JavaEnvironmentAdapter
import me.mucloud.application.mk.serverlauncher.muserver.MCJEServer
import me.mucloud.application.mk.serverlauncher.muserver.MCJEServerAdapter
import me.mucloud.application.mk.serverlauncher.muserver.MCJEServerType
import me.mucloud.application.mk.serverlauncher.muserver.MCJEServerTypeSerializer
import me.mucloud.application.mk.serverlauncher.muview.mulink.initWebSocket
import me.mucloud.application.mk.serverlauncher.muview.session.initMuSessionManager
import me.mucloud.application.mk.serverlauncher.muview.view.initEnvRoute
import me.mucloud.application.mk.serverlauncher.muview.view.initServerRoute
import kotlin.time.Duration.Companion.seconds

val MuCore: MuCoreMini = MuCoreMini

val gson: Gson = GsonBuilder()
    .setPrettyPrinting()
    .registerTypeAdapter(JavaEnvironment::class.java, JavaEnvironmentAdapter)
    .registerTypeAdapter(MCJEServer::class.java, MCJEServerAdapter)
    .registerTypeAdapter(MCJEServerType::class.java, MCJEServerTypeSerializer)
    .create()

fun main() {
    MuCore.start()
    embeddedServer(Netty, port = MuCore.getMuCoreConfig().muCorePort, module = Application::module)
        .start(wait = true)
        .addShutdownHook(MuCore::stop)
}

fun Application.installPlugins(){
    install(CORS){
        anyHost()
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Options)
        allowHeader("Content-Type")
        allowSameOrigin = true
        allowNonSimpleContentTypes = true
    }
    install(ContentNegotiation){
        gson{
            setPrettyPrinting()
            registerTypeAdapter(JavaEnvironment::class.java, JavaEnvironmentAdapter)
            registerTypeAdapter(MCJEServer::class.java, MCJEServerAdapter)
            registerTypeAdapter(MCJEServerType::class.java, MCJEServerTypeSerializer)
        }
    }
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = GsonWebsocketContentConverter(gson)
    }
    install(Sessions){

    }
}

fun Application.module() {
    installPlugins()
    initMuSessionManager()
    initServerRoute()
    initEnvRoute()
    initWebSocket()
    initMuView()
}

fun Application.initMuView(){
    routing {
        singlePageApplication {
            vue("MuView")
        }
    }
}
