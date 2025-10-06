package me.mucloud.application.mk.serverlauncher.common.api.core.server

import java.net.URI

interface MuServerType {

    fun getTypeID(): String
    fun getTypeName(): String
    fun getTypeDesc(): String

    fun getAPI4VerList(): URI
    fun getAPI4VerInfo(): URI
    fun getAPI4VerDownload(): URI

//    fun getServerTemplate(): MuServerTemplate

}