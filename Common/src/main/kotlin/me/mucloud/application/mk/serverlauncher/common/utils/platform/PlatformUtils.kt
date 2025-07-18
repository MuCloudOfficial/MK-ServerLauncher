package me.mucloud.application.mk.serverlauncher.common.utils.platform

object PlatformUtils {

    fun isWindows(): Boolean = System.getProperty("os.name").startsWith("Windows")
    fun isMac(): Boolean = System.getProperty("os.name").startsWith("Mac")

    fun getUtils(): PlatformUtilsProvider = when{
        isWindows() -> WindowsUtils
        isMac() -> MacUtils
        else -> LinuxUtils
    }

}