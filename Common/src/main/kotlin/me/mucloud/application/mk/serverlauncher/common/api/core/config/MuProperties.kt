package me.mucloud.application.mk.serverlauncher.common.api.core.config

interface MuProperties{

    fun getVersion(): Int

    fun getDescription(): String

    fun getProperty(key: String): MuProperty<*>

    fun setProperty(property: MuProperty<*>)

}