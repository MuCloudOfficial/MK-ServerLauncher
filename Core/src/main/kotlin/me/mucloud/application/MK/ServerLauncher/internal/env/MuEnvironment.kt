package me.mucloud.application.MK.ServerLauncher.internal.env

import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor

/**
 * An Environment.
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

    companion object{
        fun descriptor() = buildClassSerialDescriptor("MuEnvironment"){
            element("location", String.serializer().descriptor)
            element("name", String.serializer().descriptor)
            element("version", String.serializer().descriptor)
        }
    }
}