package me.mucloud.application.mk.serverlauncher.common.env

import me.mucloud.application.mk.serverlauncher.common.server.JavaVersion
import java.io.File
import java.io.FileReader

/**
 * # | MuExtension - MCJEServer
 *
 * ## Java Environment
 *
 * @since DEV.1
 * @author Mu_Cloud
 * @param name JavaEnvironment Name
 * @param path Java Installation Folder (like %JAVA_HOME% Folder)
 */
data class JavaEnvironment(
    private val name: String,
    private val path: String,
){

    // Java Distribution Name
    val distributionName: String
        get() = getReleaseProperty("IMPLEMENTOR_VERSION") ?: "Unknown"

    // Java Version
    private val version: String
        get() = getReleaseProperty("JAVA_VERSION") ?: "Unknown"

    /**
     * Get the Content of File named "RELEASE" in the Java Installation Folder
     *
     * @return The File Content as Map
     */
    private fun getReleaseFileContent(): Map<String, String>{
        val releaseFile = File(path).resolve("release")
        val map = mutableMapOf<String, String>()
        if(!releaseFile.exists()) return map
        FileReader(releaseFile).useLines { l ->
            val split = l.toString().split("=")
            map.put(split[0], split[1])
        }
        return map
    }

    /**
     * Get the Value of the [getReleaseFileContent] Map by [property] Key
     *
     * @param property Key in Java RELEASE File
     * @return value of the Java RELEASE File
     */
    private fun getReleaseProperty(property: String): String? = getReleaseFileContent()[property]

    /**
     * Get the Name of JavaEnvironment
     *
     * @return the name of this JavaEnvironment
     */
    fun getName(): String  = name

    /**
     * Get the Version of JavaEnvironment
     *
     * @return the version of JavaEnvironment as [String]
     */
    fun getVersionString(): String = version

    /**
     * Get the Version of JavaEnvironment
     *
     * @return the version of JavaEnvironment as [JavaVersion]
     */
    fun getVersion(): JavaVersion = JavaVersion.get(this)


    /**
     * Get the Executable File
     *
     * @return The executable file of JavaEnvironment, which usually refers to the "java.exe" file
     */
    fun getExecFolder(): File = File(path)
}