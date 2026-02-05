package me.mucloud.application.mk.serverlauncher.common.server.mcserver

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8

abstract class MCJEServerType(
    val id: String,
    val name: String,
    val desc: String,
) {
    /**
     * 获取该服务器种类对应的支持版本列表
     *
     * @return 一个带有版本号的 [List]
     *
     * @since DEV.1
     * @author Mu_Cloud
     */
    abstract fun getVerListAPI(): URL

    /**
     * 获取指定版本或构建号的服务器内核下载链接
     *
     * @param ver
     * @param build 构建号，默认是 0
     * @return 以字符串形式表示的链接
     *
     * @since DEV.1
     * @author Mu_Cloud
     */
    abstract fun getCoreLinkByVerAPI(ver: String): URL

    data class CoreMeta(
        val project: String,
        val version: String,
        val build: String,
        val url: String
    )

    open fun resolveCoreJar(folder: File): File = folder.resolve("core.jar")

    open fun cacheFile(folder: File): File = folder.resolve("version.json")

    open fun readVersionCache(folder: File): JsonObject? {
        val f = cacheFile(folder)
        if (!f.exists()) return null
        return runCatching { JsonParser.parseReader(f.reader(UTF_8)).asJsonObject }.getOrNull()
    }

    open fun writeVersionCache(folder: File, meta: CoreMeta) {
        val f = cacheFile(folder)
        val json = JsonObject().apply {
            addProperty("project", meta.project)
            addProperty("version", meta.version)
            addProperty("build", meta.build)
            addProperty("url", meta.url)
            addProperty("updatedAt", System.currentTimeMillis())
        }
        FileWriter(f, UTF_8).use { it.write(json.toString()) }
    }

    abstract fun fetchLatestBuild(version: String): CoreMeta?

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MCJEServerType) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (desc != other.desc) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + desc.hashCode()
        return result
    }

    companion object{
        val UNKNOWN = object: MCJEServerType("unknown", "Unknown", "Unknown Server Type"){
            override fun getVerListAPI(): URL = URL("")
            override fun getCoreLinkByVerAPI(ver: String): URL = URL("")
            override fun fetchLatestBuild(version: String): CoreMeta? = null
        }

        val PAPER = object: MCJEServerType("paper", "PaperSpigot", "A High Performance Server based on Spigot"){
            override fun getVerListAPI(): URL = URL("")
            override fun getCoreLinkByVerAPI(ver: String): URL = URL("")

            override fun fetchLatestBuild(version: String): CoreMeta? {
                return fetchLatestBuildFromApi(
                    project = "paper",
                    baseApi = "https://api.papermc.io/v2/projects",
                    version = version,
                    filePattern = "paper-%s-%s.jar"
                )
            }
        }

        val LEAVES = object: MCJEServerType("leaves", "Leaves", "A High Performance Server with Fixed Broken Features"){
            override fun getVerListAPI(): URL = URL("")
            override fun getCoreLinkByVerAPI(ver: String): URL = URL("")

            override fun fetchLatestBuild(version: String): CoreMeta? {
                return fetchLatestBuildFromApi(
                    project = "leaves",
                    baseApi = "https://api.leavesmc.org/v2/projects",
                    version = version,
                    filePattern = "leaves-%s-%s.jar"
                )
            }
        }
    }

    protected fun fetchLatestBuildFromApi(
        project: String,
        baseApi: String,
        version: String,
        filePattern: String
    ): CoreMeta? {
        return runCatching {
            val verUrl = "$baseApi/$project/versions/$version"
            val verJson = JsonParser.parseReader(InputStreamReader(URL(verUrl).openStream(), UTF_8)).asJsonObject
            val builds = verJson.getAsJsonArray("builds")
            if (builds.size() == 0) {
                return null
            }
            val latestBuild = builds.get(builds.size() - 1).asString
            val fileName = String.format(filePattern, version, latestBuild)
            val downloadUrl = "$baseApi/$project/versions/$version/builds/$latestBuild/downloads/$fileName"
            CoreMeta(project, version, latestBuild, downloadUrl)
        }.getOrNull()
    }
    }

}

object ServerTypeSerializer: JsonSerializer<MCJEServerType>{
    override fun serialize(
        s: MCJEServerType,
        t: Type,
        c: JsonSerializationContext
    ): JsonElement {
        return JsonObject().apply {
            addProperty("id", s.id)
            addProperty("name", s.name)
            addProperty("desc", s.desc)
        }
    }

}