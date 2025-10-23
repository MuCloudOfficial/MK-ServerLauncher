package me.mucloud.application.mk.serverlauncher.common.env

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.StandardCharsets

class EnvPool {

    // Java Envs
    private val jEnvs: MutableList<JavaEnvironment> = mutableListOf()

    // JavaEnvironments  File
    private val envFile = File("env.json")

    /**
     * Init of EnvPool
     */
    init {
        if(!envFile.exists()) {
            envFile.createNewFile()
            FileWriter(envFile).apply { write("[]"); flush() }
        }
        scanLocalJavaEnv()
    }

    /**
     * Scan the System Java Installation as JavaEnvironment named "SysEnv"
     *
     * For now, it will only scan the "JAVA_HOME" system environment to locate the Java Installation in System
     *
     * This Function Implementation may change Frequently
     */
    private fun scanLocalJavaEnv(){
        val sysEnvPath = System.getenv("JAVA_HOME")
        JavaEnvironment("SysEnv", sysEnvPath)
    }

    /**
     * Scan JavaEnvironment from env.json in MK-ServerLauncher Installation Folder
     *
     * @return List of JavaEnvironment, element deserialized by JavaEnvironmentAdapter
     */
    fun scanEnv() =
        Gson().fromJson<List<JavaEnvironment>>(
            FileReader(envFile.also { if(!it.exists()) return }, StandardCharsets.UTF_8),
            object: TypeToken<List<JavaEnvironment>>(){}.type
        ).forEach { e ->
            jEnvs.add(e)
        }

    /**
     * Write [jEnvs] Object to [envFile] by JavaEnvironmentAdapter
     */
    fun save(){
        FileWriter(envFile.also { if (!it.exists()) it.createNewFile() }, StandardCharsets.UTF_8).also {
            it.write(GsonBuilder().setPrettyPrinting().create().toJson(jEnvs))
            it.flush()
        }
    }

    fun getEnv(name: String) = jEnvs.find { it.getName() == name }

    fun delEnv(envName: String): Boolean{
        return jEnvs.removeIf { it.getName() == envName }.also{ save() }
    }

    fun regEnv(env: JavaEnvironment){
        jEnvs.find { it.getName() == env.getName() || it.getExecFolder() == env.getExecFolder() } ?: {
            jEnvs.add(env)
            save()
        }
    }

    fun getEnvList(): List<JavaEnvironment> = jEnvs

}