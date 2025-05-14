package me.mucloud.application.MK.ServerLauncher.internal.env

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.mucloud.application.MK.ServerLauncher.internal.manage.Configuration
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.StandardCharsets

object EnvPool {

    private val POOL: MutableList<JavaEnvironment> = mutableListOf()
    private val envFile = File(Configuration.getConfigurationFolder(), "env.json")

    init {
        if(!envFile.exists()) envFile.createNewFile()
    }

    fun readConfig(){
        POOL +Gson().fromJson<List<JavaEnvironment>>(FileReader(envFile.also { if(!it.exists()) return }, StandardCharsets.UTF_8), List::class.java)
    }

    fun save(){
        FileWriter(envFile.also { if(!it.exists()) it.createNewFile() }, StandardCharsets.UTF_8).also {
            it.write(GsonBuilder().setPrettyPrinting().create().toJson(POOL))
            it.flush()
        }
    }

    fun getEnv(name: String) = POOL.find { it.getEnvName() == name }
    fun getEnv(name: String, path: String) = POOL.find { it.getEnvName() == name || it.getLocation() == path }

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

    fun deleteEnv(index: Int){
        POOL.removeAt(index)
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

    fun getEnvList(): List<JavaEnvironment>{
        return POOL
    }

}