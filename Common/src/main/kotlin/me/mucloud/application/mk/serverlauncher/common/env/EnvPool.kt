package me.mucloud.application.mk.serverlauncher.common.env

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import me.mucloud.application.mk.serverlauncher.common.manage.ConfigurationFactory

object EnvPool {

    private val POOL: MutableList<JavaEnvironment> = mutableListOf()
    private val envFile = File(ConfigurationFactory.getConfigurationFolder(), "env.json")

    init {
        // regCurrentJavaEnvironment() todo
        if(!envFile.exists()) {
            envFile.createNewFile()
            FileWriter(envFile).also { it.write("[]"); it.flush() }
        }
    }

    fun scanEnv(){
        Gson().fromJson<List<JavaEnvironment>>(
            FileReader(envFile.also { if(!it.exists()) return }, StandardCharsets.UTF_8),
            object: TypeToken<List<JavaEnvironment>>(){}.type
        ).forEach { e ->
            POOL.add(e)
        }
    }

    fun save(){
        FileWriter(envFile.also { if(!it.exists()) it.createNewFile() }, StandardCharsets.UTF_8).also {
            it.write(GsonBuilder().setPrettyPrinting().create().toJson(POOL))
            it.flush()
        }
    }

    fun getEnv(name: String) = POOL.find { it.name == name }
    internal fun getEnv(name: String, path: String) = POOL.find { it.name == name || it.path == path }

    internal fun deleteEnv(env: JavaEnvironment){ POOL.remove(env) }

    fun deleteEnv(name: String): Boolean{
        getEnv(name).let {
            if (it != null) {
                POOL.remove(it)
                return true
            }else{
                return false
            }
        }
    }

    fun addEnv(name: String, path: String): Boolean{
        getEnv(name, path).let {
            if (it != null) {
                return false
            }else{
                POOL.add(JavaEnvironment(name, path))
                return true
            }
        }
    }

    fun getEnvList(): List<JavaEnvironment> = POOL

}