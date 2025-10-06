package me.mucloud.application.mk.serverlauncher.common.utils

import me.mucloud.application.mk.serverlauncher.common.api.core.env.MuEnvironment
import me.mucloud.application.mk.serverlauncher.common.api.core.server.MuServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val CORE_LOGGER: Logger = LoggerFactory.getLogger("MK-ServerLauncher | MuCore | ")
val VIEW_LOGGER: Logger = LoggerFactory.getLogger("MK-ServerLauncher | MuView | ")

val MUSV_LOGGER: (MuServer) -> Logger = { LoggerFactory.getLogger("MK-ServerLauncher | MuServer | ${it.getName()} | ") }
val MUEV_LOGGER: (MuEnvironment) -> Logger = { LoggerFactory.getLogger("MK-ServerLauncher | MuEnvironment | ${it.getName()} | ") }