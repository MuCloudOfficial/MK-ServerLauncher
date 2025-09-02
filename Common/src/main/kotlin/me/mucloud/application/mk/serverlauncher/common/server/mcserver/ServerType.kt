package me.mucloud.application.mk.serverlauncher.common.server.mcserver

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import kotlinx.serialization.Serializable

@Serializable
abstract class ServerType(
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
    abstract fun getVerList(): List<String>

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
    abstract fun getCoreLinkByVer(ver: String): String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ServerType) return false

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
        val Spigot: ServerType
            get() = object: ServerType("spigot", "Spigot", "Spigot Server Core"){
                override fun getVerList(): List<String> {
                    TODO("Not yet implemented")
                }

                override fun getCoreLinkByVer(ver: String): String {
                    TODO("Not yet implemented")
                }
            }

        val Paper: ServerType
            get() = object: ServerType("paper", "Paper", ""){
                override fun getVerList(): List<String> {
                    TODO("Not yet implemented")
                }

                override fun getCoreLinkByVer(ver: String): String {
                    TODO("Not yet implemented")
                }
            }

        val Leave: ServerType
            get() = object: ServerType("leaves", "Leaves", ""){
                override fun getVerList(): List<String> {
                    TODO("Not yet implemented")
                }

                override fun getCoreLinkByVer(ver: String): String {
                    TODO("Not yet implemented")
                }
            }

        val Unknown: ServerType
            get() = object: ServerType("unknown", "Unknown", "Unknown"){
                override fun getVerList(): List<String> = emptyList()
                override fun getCoreLinkByVer(ver: String): String = ""
            }
    }
}

object ServerTypeSerializer: JsonSerializer<ServerType>{
    override fun serialize(
        s: ServerType,
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