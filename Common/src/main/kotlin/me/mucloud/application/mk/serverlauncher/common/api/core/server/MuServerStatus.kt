package me.mucloud.application.mk.serverlauncher.common.api.core.server

enum class MuServerStatus {

    STOPPED(0),
    PREPARING(1),
    STARTING(2),
    RUNNING(3),
    STOPPING(4),
    ;

    private val id: Byte

    constructor(id: Byte){ this.id = id }

}