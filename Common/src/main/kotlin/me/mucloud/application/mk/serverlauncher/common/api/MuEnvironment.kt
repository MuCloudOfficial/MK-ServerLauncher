package me.mucloud.application.mk.serverlauncher.common.api

import java.io.File

interface MuEnvironment {

    fun getName(): String

    fun getVersionString(): String

    fun getVersion(): Int

    fun getExecFile(): File

}