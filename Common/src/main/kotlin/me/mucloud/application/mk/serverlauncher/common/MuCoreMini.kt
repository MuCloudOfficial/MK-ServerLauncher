package me.mucloud.application.mk.serverlauncher.common

import me.mucloud.application.mk.serverlauncher.common.external.AppInfoStatus

object MuCoreMini {

    private val MuCoreInfo = AppInfoStatus("MuCore HPE DEV Mini", "TinyNova V1 DEV.1")

    fun startMuCore(){

    }

    fun getMuCoreInfo() : AppInfoStatus = MuCoreInfo

}