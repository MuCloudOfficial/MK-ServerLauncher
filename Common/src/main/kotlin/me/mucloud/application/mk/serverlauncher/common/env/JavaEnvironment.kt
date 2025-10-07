package me.mucloud.application.mk.serverlauncher.common.env

import kotlinx.serialization.Serializable
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.JavaVersion
import java.io.File
import java.io.FileReader

/**
 * Java Environment
 *
 * MuPack Internal Environment Template
 *
 * @since DEV.1
 * @author Mu_Cloud
 * @param name JavaEnvironment Name
 * @param path Java Installation Folder (like %JAVA_HOME% Folder)
 */
@Serializable
data class JavaEnvironment(
    private val name: String,
    private val path: String,
){
    private lateinit var version: String

    init {
        val position = File(path)
        if(position.exists() && position.isDirectory){
            val verFile = File(position, "release")
            FileReader(verFile).useLines { l ->
                l.find { it.startsWith("JAVA_VERSION=") }?.let {
                    version = it.split("=")[1].trim().substring(1).dropLast(1)
                }
            }
        }
    }

    fun getName(): String  = name
    fun getVersionString(): String = version
    fun getVersion(): Int = JavaVersion.get(this).code
    fun getExecFile(): File = File(path).resolve("bin/java.exe")
}