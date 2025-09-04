package me.mucloud.application.mk.serverlauncher.common.api

import java.io.File

/**
 *  # | MuEnvironment
 *  **Environment 4 MuServer**
 *
 * @author Mu_Cloud
 * @since Hyper MPE Mini | V1
 */
interface MuEnvironment {

    /**
     *  ## | MuEnvironment
     *  Get Environment Name
     *
     *  @return Environment Name by Custom
     *
     *  @author Mu_Cloud
     *  @since Hyper MPE Mini | V1
     */
    fun getName(): String

    /**
     *  ## | MuEnvironment
     *  Get Environment Version
     *
     *  **Important: the return Value will show in MuView**
     *
     *  @return Environment Version Name
     *
     *  @author Mu_Cloud
     *  @since Hyper MPE Mini | V1
     */
    fun getVersionString(): String

    /**
     *  ## | MuEnvironment
     *  Get Environment Version Code
     *
     *  @return Environment Version
     *
     *  @author Mu_Cloud
     *  @since Hyper MPE Mini | V1
     */
    fun getVersion(): Int

    /**
     *  ## | MuEnvironment
     *
     *  Get Environment Executable File
     *
     *  @return The Environment Executable File for CLI
     *
     *  @author Mu_Cloud
     *  @since Hyper MPE Mini | V1
     */
    fun getExecFile(): File

}