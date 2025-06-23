package me.mucloud.application.mk.serverlauncher.dpe

class JVMPlatform {
    val name: String = "Java ${System.getProperty("java.version")}"
}

fun getPlatform() = JVMPlatform()