package me.mucloud.application.MK.ServerLauncher.internal.env

import kotlinx.serialization.Serializable

/**
 * Java Environment
 *
 * MuPack Internal Environment Template
 *
 * @since DEV.16
 * @author Mu_Cloud
 */
@Serializable
data class JavaEnvironment(
    val name: String,
    val version: String,
    val path: String
): MuEnvironment{
    override fun getLocation(): String = path
    override fun getEnvName(): String = name
    override fun getEnvVersion(): String = version
}
