package me.mucloud.application.mk.serverlauncher.common.env

import java.io.File
import java.io.FileReader
import kotlinx.serialization.Serializable
import me.mucloud.application.mk.serverlauncher.common.api.MuEnvironment
import me.mucloud.application.mk.serverlauncher.common.server.mcserver.JavaVersion

/**
 * Java Environment
 *
 * MuPack Internal Environment Template
 *
 * @since DEV.1
 * @author Mu_Cloud
 */
@Serializable
data class JavaEnvironment(
    val name: String,
    val path: String
): MuEnvironment{
    var version: String = "Unknown"

    init {
        val position = File(path).parentFile.parentFile
        if(position.exists() && position.isDirectory){
            val verFile = File(position, "release")
            FileReader(verFile).useLines { l ->
                l.find { it.startsWith("JAVA_VERSION=") }?.let {
                    version = it.split("=")[1].trim().substring(1).dropLast(1)
                }
            }
        }
    }

    override fun getName(): String  = name

    override fun getVersionString(): String = version

    override fun getVersion(): Int = JavaVersion.get(this).code

    override fun getExecFile(): File = File(path)
}