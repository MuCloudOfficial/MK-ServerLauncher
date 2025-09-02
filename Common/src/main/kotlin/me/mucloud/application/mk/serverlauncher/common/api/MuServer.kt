package me.mucloud.application.mk.serverlauncher.common.api

interface MuServer {

    fun getServerName(): String

    fun getServerDefaultPort(): Int

    fun getServerPort(): Int

    fun getServerDescription(): String

    fun getServerVersion(): String

    fun start()

    fun stop()

    fun restart()

    fun getConfiguration(): MuServerConfiguration
}