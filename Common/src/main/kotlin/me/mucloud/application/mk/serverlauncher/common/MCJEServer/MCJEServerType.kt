package me.mucloud.application.mk.serverlauncher.common.MCJEServer

import me.mucloud.application.mk.serverlauncher.common.api.core.server.MuServerType
import java.net.URI

abstract class MCJEServerType(
    val id: String,
    val name: String,
    val desc: String,
): MuServerType {

    final override fun getTypeID(): String = "mcjeserver:$id"
    final override fun getTypeName(): String = name
    final override fun getTypeDesc(): String = desc

    override fun getAPI4VerList(): URI {
        TODO("Not yet implemented")
    }

    override fun getAPI4VerInfo(): URI {
        TODO("Not yet implemented")
    }

    override fun getAPI4VerDownload(): URI {
        TODO("Not yet implemented")
    }

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
}