package ie.setu.helpers

import ie.setu.config.JavalinConfig

object ServerContainer {

    val instance by lazy {
        startServerContainer()
    }

    private fun startServerContainer() = JavalinConfig().startJavalinService()

}
