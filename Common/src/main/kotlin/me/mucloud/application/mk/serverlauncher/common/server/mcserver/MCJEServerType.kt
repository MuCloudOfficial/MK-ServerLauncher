package me.mucloud.application.mk.serverlauncher.common.server.mcserver

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.net.URL

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
        }

        val PAPER = object: MCJEServerType("paper", "PaperSpigot", "A High Performance Server based on Spigot"){
            override fun getVerListAPI(): URL = URL("")
            override fun getCoreLinkByVerAPI(ver: String): URL = URL("")
        }

        val LEAVES = object: MCJEServerType("leaves", "Leaves", "A High Performance Server with Fixed Broken Features"){
            override fun getVerListAPI(): URL = URL("")
            override fun getCoreLinkByVerAPI(ver: String): URL = URL("")
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