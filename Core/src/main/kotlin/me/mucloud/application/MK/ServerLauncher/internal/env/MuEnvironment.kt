package me.mucloud.application.MK.ServerLauncher.internal.env

/**
 * A Environment.
 *
 * Only API.
 *
 * ! Internal, may not implement !
 *
 * @since DEV.16
 * @author Mu_Cloud
 */
interface MuEnvironment {
    fun getLocation(): String
    fun getEnvName(): String
    fun getEnvVersion(): String
}