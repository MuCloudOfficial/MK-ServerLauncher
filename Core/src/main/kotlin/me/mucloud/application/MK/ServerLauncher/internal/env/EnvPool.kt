package me.mucloud.application.MK.ServerLauncher.internal.env

object EnvPool {

    private val POOL: MutableList<JavaEnvironment> = mutableListOf()

    fun getEnv(name: String) = POOL.find { it.getEnvName() == name }
    fun getEnv(name: String, path: String) = POOL.find { it.getEnvName() == name || it.getLocation() == path }

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

    fun deleteEnv(index: Int){
        POOL.removeAt(index)
    }

    fun addEnv(name: String, path: String): Boolean{
        getEnv(name, path).let {
            if (it != null) {
                return false
            }else{
                POOL.add(JavaEnvironment(name, path))
                return true
            }
        }
    }

    fun getEnvList(): List<JavaEnvironment>{
        return POOL
    }

}