package me.mucloud.application.mk.serverlauncher.common.env

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 *  Java Environment Serializer
 */
object JavaEnvironmentAdapter: JsonSerializer<JavaEnvironment>{

    override fun serialize(s: JavaEnvironment, t: Type, c: JsonSerializationContext): JsonElement  =
        JsonObject().apply{
            addProperty("EV_NAME", s.getName())
            addProperty("EV_VER", "${s.distributionName}_${s.getVersionString()}")
            addProperty("EV_CODE", s.getVersion().code)
            addProperty("EV_LOC", s.getExecFolder().absolutePath)
        }

}