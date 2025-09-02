package me.mucloud.application.mk.serverlauncher.common.api

interface MuServerConfiguration{

    fun getVersion(): Int

    fun getProperty(key: String): MuProperty<*>

    fun setProperty(property: MuProperty<*>)

}