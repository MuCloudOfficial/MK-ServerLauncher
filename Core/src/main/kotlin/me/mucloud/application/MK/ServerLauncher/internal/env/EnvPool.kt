package me.mucloud.application.MK.ServerLauncher.internal.env

object EnvPool {

    private val POOL: MutableList<MuEnvironment> = mutableListOf(
        JavaEnvironment("zulu11", "11.0.16", "/"),
        JavaEnvironment("zulu16", "16.0.3", "/"),
    )

    fun getEnv(name: String) = POOL.find { it.getEnvName() == name }

    fun deleteEnv(name: String): Boolean{
        getEnv(name).let {
            if (it != null) {
                POOL.remove(it)
                return true
            }else{
                return false
            }
        }
    }

    fun addEnv(name: String): Boolean{
        getEnv(name).let {
            if (it != null) {
                POOL.add(it)
                return true
            }else{
                return false
            }
        }
    }

    fun getEnvList(): List<MuEnvironment>{
        return POOL
    }

}