package me.mucloud.application.MK.ServerLauncher.internal.env

import kotlinx.serialization.Serializable
import me.mucloud.application.MK.ServerLauncher.internal.server.mcserver.JavaVersion
import java.io.File
import java.io.FileReader

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
){
    var version: String = "Unknown"

    init {
        val file = File(path)
        if(file.exists() && file.isDirectory){
            val verFile = File(file, "release")
            FileReader(verFile).useLines { l ->
                l.find { it.startsWith("JAVA_VERSION=") }?.let {
                    version = it.split("=")[1].trim().substring(1).dropLast(1)
                }
            }
        }
    }

    fun getCodeVersion(): JavaVersion = JavaVersion.valueOf(version)
}
