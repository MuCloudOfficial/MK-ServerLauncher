package me.mucloud.application.mk.serverlauncher.common.server

import java.io.File
import java.nio.file.Path

object StandardMCJEServerTypes {
    val VANILLA = object : MCJEServerType("vanilla", false, "Vanilla", "Vanilla Server Core") {
        override fun getAvailableVersions(): List<String> {
            TODO("Not yet implemented")
        }

        override fun getServerCore(vercode: String): File {
            TODO("Not yet implemented")
        }

        override fun getServerCoreSettingsFile(): List<Path> {
            TODO("Not yet implemented")
        }
    }

    val SPIGOT = object : MCJEServerType("spigot", false, "Spigot", "A Common and widely used Server Code") {
        override fun getAvailableVersions(): List<String> {
            TODO("Not yet implemented")
        }

        override fun getServerCore(vercode: String): File {
            TODO("Not yet implemented")
        }

        override fun getServerCoreSettingsFile(): List<Path> {
            TODO("Not yet implemented")
        }
    }

    val PAPER = object : MCJEServerType("paper", false, "PaperSpigot", "A High-Performance Server based on Spigot") {
        override fun getAvailableVersions(): List<String> {
            TODO("Not yet implemented")
        }

        override fun getServerCore(vercode: String): File {
            TODO("Not yet implemented")
        }

        override fun getServerCoreSettingsFile(): List<Path> {
            TODO("Not yet implemented")
        }
    }

    val LEAVES = object : MCJEServerType("leaves", false, "Leaves", "Leaves") {
        override fun getAvailableVersions(): List<String> {
            TODO("Not yet implemented")
        }

        override fun getServerCore(vercode: String): File {
            TODO("Not yet implemented")
        }

        override fun getServerCoreSettingsFile(): List<Path> {
            TODO("Not yet implemented")
        }

    }

    val FOLIA = object : MCJEServerType("folia", false, "Folia", "A High-Performance and Multi-Thread featured Server Code") {
        override fun getAvailableVersions(): List<String> {
            TODO("Not yet implemented")
        }

        override fun getServerCore(vercode: String): File {
            TODO("Not yet implemented")
        }

        override fun getServerCoreSettingsFile(): List<Path> {
            TODO("Not yet implemented")
        }
    }

    val UNKNOWN = object : MCJEServerType("unknown", false, "Unknown", "Unknown") {
        override fun getAvailableVersions(): List<String> = emptyList()
        override fun getServerCore(vercode: String): File { throw UnsupportedOperationException("UNKNOWN SERVER TYPE IS NOT SUPPORTED!") }
        override fun getServerCoreSettingsFile(): List<Path> = emptyList()
    }
}