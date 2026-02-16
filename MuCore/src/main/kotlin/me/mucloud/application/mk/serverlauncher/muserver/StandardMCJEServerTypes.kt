package me.mucloud.application.mk.serverlauncher.muserver

import com.google.gson.JsonElement
import me.mucloud.application.mk.serverlauncher.mucore.external.MuHTTPClient
import java.io.File
import java.net.URL
import java.nio.file.Path

object StandardMCJEServerTypes {

    private const val MOJANG_VERSION_MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest.json"
    private const val PAPER_API = "https://fill.papermc.io/v3/projects/paper"
    private const val FOLIA_API = "https://fill.papermc.io/v3/projects/folia"
    private const val LEAVES_API = "https://api.leavesmc.org/v2/projects/leaves"

    private fun downloadToTemp(url: String, prefix: String): File {
        val dir = File(System.getProperty("java.io.tmpdir"), "mk-serverlauncher/core-cache")
        if (!dir.exists()) dir.mkdirs()
        val target = File(dir, "$prefix-core.jar")
        URL(url).openStream().use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return target
    }

    val VANILLA = object : MCJEServerType("vanilla", false, "Vanilla", "Vanilla Server Core") {
        private var tmpMeta: Map<String, URL> = emptyMap()

        override fun getAvailableVersions(): List<String>{
            val rawVersions = MuHTTPClient.getJsonArray(MOJANG_VERSION_MANIFEST, "versions")
            tmpMeta = rawVersions.associate{
                val l = it.asJsonObject
                l["id"].asString to URL(l["url"].asString)
            }
            return tmpMeta.keys.toList()
        }

        override fun getCoreDownloadLink(vercode: String): URL {
            if(tmpMeta[vercode] != null) {
                return tmpMeta[vercode]!!
            }else{
                throw RuntimeException("Versions not found: $vercode")
            }
        }

        override fun getServerCoreSettingsFile(): List<Path> = emptyList()
    }

//    val SPIGOT = object : MCJEServerType("spigot", false, "Spigot", "A Common and widely used Server Code") {
//        override fun getAvailableVersions(): List<String>
//        override fun getCoreDownloadLink(vercode: String): URL
//        override fun getServerCoreSettingsFile(): List<Path>
//    }

    val PAPER = object : MCJEServerType("paper", false, "PaperSpigot", "A High-Performance Server based on Spigot") {
        override fun getAvailableVersions(): List<String> =
            MuHTTPClient.getJsonArray(PAPER_API, "versions")
                .flatMap { it.asJsonArray.map(JsonElement::getAsString) } // todo: Check Required.
                .toList()

        override fun getCoreDownloadLink(vercode: String): URL{
            var raw = MuHTTPClient.getJsonObject("$PAPER_API/versions/$vercode/build/latest")
                .asJsonObject["downloads"]
                .asJsonObject["url"]
                .asString
            return URL(raw)
        }

        override fun getServerCoreSettingsFile(): List<Path> = emptyList() // todo: PaperSpigot Server Specified Setting File Structure
    }

    val FOLIA = object : MCJEServerType("folia", false, "Folia", "A High-Performance and Multi-Thread featured Server Code") {
        override fun getAvailableVersions(): List<String> =
            MuHTTPClient.getJsonArray(FOLIA_API, "versions")
                .flatMap { it.asJsonArray.map(JsonElement::getAsString) }
                .toList()

        override fun getCoreDownloadLink(vercode: String): URL {
            val raw = MuHTTPClient.getJsonObject("$FOLIA_API/versions/$vercode/builds/latest")
                .asJsonObject["downloads"]
                .asJsonObject["url"]
                .asString
            return URL(raw)
        }

        override fun getServerCoreSettingsFile(): List<Path> = emptyList() // todo: Folia Server Specified Setting File Structure
    }

    val LEAVES = object : MCJEServerType("leaves", false, "Leaves", "Leaves") {
        override fun getAvailableVersions(): List<String> =
            MuHTTPClient.getJsonArray(LEAVES_API, "versions")
                .map { it.asString }


        override fun getCoreDownloadLink(vercode: String): URL {
            val latestBuild = MuHTTPClient.getJsonObject("$LEAVES_API/versions/$vercode")
                .asJsonObject["builds"]
                .asJsonArray.toList().last()
            val raw = "$LEAVES_API/versions/$vercode/application"
            return URL(raw)
        }

        override fun getServerCoreSettingsFile(): List<Path> = emptyList()
    }

    val UNKNOWN = object : MCJEServerType("unknown", false, "Unknown", "Unknown") {
        override fun getAvailableVersions(): List<String> = emptyList()
        override fun getCoreDownloadLink(vercode: String): URL { throw UnsupportedOperationException("UNKNOWN SERVER TYPE IS NOT SUPPORTED!") }
        override fun getServerCoreSettingsFile(): List<Path> = emptyList()
    }
}
