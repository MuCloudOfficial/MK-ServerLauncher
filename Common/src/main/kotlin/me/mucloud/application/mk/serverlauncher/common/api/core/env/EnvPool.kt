package me.mucloud.application.mk.serverlauncher.common.api.core.env

import com.google.gson.GsonBuilder
import me.mucloud.application.mk.serverlauncher.common.manage.ConfigurationFactory
import java.io.File
import java.io.FileWriter
import java.nio.charset.StandardCharsets

object EnvPool {

    private val POOL: MutableList<MuEnvironment> = mutableListOf()
    private val envFile = File(ConfigurationFactory.getConfigurationFolder(), "env.json")

    init {
        // regCurrentJavaEnvironment() todo
        if(!envFile.exists()) {
            envFile.createNewFile()
            FileWriter(envFile).also { it.write("[]"); it.flush() }
        }
    }

    fun save(){
        FileWriter(envFile.also { if(!it.exists()) it.createNewFile() }, StandardCharsets.UTF_8).also {
            it.write(GsonBuilder().setPrettyPrinting().create().toJson(POOL))
            it.flush()
        }
    }

    fun getEnv(name: String) = POOL.find { it.getName() == name }

    internal fun deleteEnv(env: MuEnvironment){ POOL.remove(env) }

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

    fun addEnv(env: MuEnvironment): Boolean{
        getEnv(env.getName()).let {
            if (it != null) {
                return false
            }else{
                POOL.add(env)
                return true
            }
        }
    }

    fun getEnvList(): List<MuEnvironment> = POOL

}