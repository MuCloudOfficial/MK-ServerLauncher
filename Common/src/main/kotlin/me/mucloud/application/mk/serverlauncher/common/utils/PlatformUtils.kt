package me.mucloud.application.mk.serverlauncher.common.utils

object PlatformUtils {

    fun isWindows(): Boolean = System.getProperty("os.name").startsWith("Windows")
    fun isMac(): Boolean = System.getProperty("os.name").startsWith("Mac")

}

enum class Platform{
    LINUX, WINDOWS, MAC
}