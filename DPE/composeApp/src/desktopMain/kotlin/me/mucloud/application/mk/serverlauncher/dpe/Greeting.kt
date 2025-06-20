package me.mucloud.application.mk.serverlauncher.dpe

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}