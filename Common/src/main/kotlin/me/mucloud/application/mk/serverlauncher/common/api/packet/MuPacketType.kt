package me.mucloud.application.mk.serverlauncher.common.api.packet

import me.mucloud.application.mk.serverlauncher.common.utils.CORE_LOGGER

class MuPacketType(
    val id: String,
    val opreation: String
) {
    override fun toString(): String = "$id:$opreation"

    companion object {
        // CORE CONFIG
        val CORE_INFO = MuPacketType("mucore", "info")
        val CORE_ERR = MuPacketType("mucore", "err")

        val CORE_CONFIG = MuPacketType("mucore_config", "info")
        val CORE_CONFIG_MOD = MuPacketType("mucore_config", "mod")

        // VIEW
        val VIEW_ERR = MuPacketType("muview", "err")

        // SERVER
        val SERVER_ADD = MuPacketType("muserver", "add")
        val SERVER_REMOVE = MuPacketType("muserver", "rem")
        val SERVER_MOD = MuPacketType("muserver", "mod")
        val SERVER_INFO = MuPacketType("muserver", "info")
        val SERVER_CONFIG = MuPacketType("muserver", "config")
        val SERVER_CMD_SEND = MuPacketType("muserver_cmd", "send")
        val SERVER_CMD_RECV = MuPacketType("muserver_cmd", "recv")
        val SERVER_MSG_SEND = MuPacketType("muserver_msg", "send")
        val SERVER_MSG_RECV = MuPacketType("muserver_msg", "recv")
        val SERVER_ERR = MuPacketType("muserver", "err")

        // NET REQ: SERVER
        val SERVER_LIST_NREQ = MuPacketType("muserver", "list_nreq")
        val SERVER_NREQ = MuPacketType("muserver", "nreq")

        // ENV
        val ENV_LIST = MuPacketType("muenv", "list")
        val ENV_INFO = MuPacketType("muenv", "info")
        val ENV_ADD = MuPacketType("muenv", "add")
        val ENV_DEL = MuPacketType("muenv", "del")
        val ENV_MOD = MuPacketType("muenv", "mod")

        // NET REQ: ENV
        val ENV_LIST_NREQ = MuPacketType("muenv", "list_nreq")
        val ENV_NREQ = MuPacketType("muenv", "nreq")
        val ENV_ERR = MuPacketType("muenv", "err")

        // EXP: SERVER LINK
        val SERVER_LINK = MuPacketType("muserver_link", "info")
        val SERVER_LINK_ADD = MuPacketType("muserver_link", "add")
        val SERVER_LINK_REMOVE = MuPacketType("muserver_link", "rem")
        val SERVER_LINK_ERROR = MuPacketType("muserver_link", "error")

        init{
            MuPacketTypeFactory.registerMuPacketType(
                CORE_INFO,
                CORE_ERR,
                CORE_CONFIG,
                CORE_CONFIG_MOD,
                VIEW_ERR,
                SERVER_ADD,
                SERVER_REMOVE,
                SERVER_MOD,
                SERVER_INFO,
                SERVER_CONFIG,
                SERVER_CMD_SEND,
                SERVER_CMD_RECV,
                SERVER_MSG_SEND,
                SERVER_MSG_RECV,
                SERVER_ERR,
                SERVER_LIST_NREQ,
                SERVER_NREQ ,
                ENV_LIST,
                ENV_INFO,
                ENV_ADD,
                ENV_DEL,
                ENV_MOD,
                ENV_LIST_NREQ,
                ENV_NREQ,
                ENV_ERR,
                SERVER_LINK,
                SERVER_LINK_ADD,
                SERVER_LINK_REMOVE,
                SERVER_LINK_ERROR,
            )
        }
    }
}

object MuPacketTypeFactory{

    val MuPacketTypePool = mutableListOf<MuPacketType>()

    fun registerMuPacketType(id: String, opreation: String): MuPacketType{
        return MuPacketType(id, opreation).also {
            registerMuPacketType(it)
        }
    }

    fun registerMuPacketType(vararg mpType: MuPacketType){
        mpType.forEach(::registerMuPacketType)
    }

    private fun registerMuPacketType(mpType: MuPacketType){
        CORE_LOGGER.info("Registering MuPacketType > ${mpType.id}:${mpType.opreation}")
        MuPacketTypePool.add(mpType)
    }

    fun unregisterMuPacketType(pid: String) {
        CORE_LOGGER.info("Unregistering MuPacketType > $pid")
        MuPacketTypePool.removeIf { it.id == pid }
    }
    fun unregisterAll(id: String) {
        CORE_LOGGER.info("Unregistering MuPacketTypes by class $id")
        MuPacketTypePool.removeIf { it.id == id }
    }

}