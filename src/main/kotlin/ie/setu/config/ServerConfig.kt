package ie.setu.config

import ie.setu.controllers.HealthTrackerController
import ie.setu.utils.jsonObjectMapper
import io.javalin.Javalin
import io.javalin.config.JavalinConfig
import io.javalin.json.JavalinJackson
import io.javalin.plugin.bundled.JavalinVuePlugin
import io.javalin.vue.VueComponent


class ServerConfig {

    fun startJavalinService(port: Int = getRemoteAssignedPort()): Javalin {

        val app = Javalin.create { config ->
            config.jsonMapper(
                JavalinJackson(jsonObjectMapper())
            )
            config.registerPlugin(
                JavalinVuePlugin { vue ->
                    vue.vueInstanceNameInJs = "app"
                }
            )
            config.staticFiles.add("/META-INF/resources")
            config.routes.exception(Exception::class.java) { e, ctx ->
                e.printStackTrace()
            }
            config.routes.error(404) { ctx ->
                ctx.json("404 - Not Found")
            }
            registerRoutes(config)
        }.start(port)

        return app
    }



    private fun registerRoutes(config: JavalinConfig) {
        val controller = HealthTrackerController()
        config.routes.get("/api/users", controller::getAllUsers)
        config.routes.get("/api/users/{user-id}", controller::getUserByUserId)
        config.routes.post("/api/users", controller::addUser)
        config.routes.get("api/users/email/{email}", controller::getUserByEmail)
        config.routes.delete("/api/users/{user-id}", controller::deleteUser)
        config.routes.patch("/api/users/{user-id}", controller::updateUser)
        config.routes.get("/api/activities", controller::getAllActivities)
        config.routes.post("/api/activities", controller::addActivity)
        config.routes.get("/api/users/{user-id}/activities", controller::getActivitiesByUserId)
        config.routes.delete("/api/users/{user-id}/activities", controller::deleteActivityByUserId)
        config.routes.delete("/api/activities/{activity-id}", controller::deleteActivityByActivityId)
        config.routes.patch("/api/activities/{activity-id}", controller::updateActivity)
        config.routes.get("/api/activities/{activity-id}", controller::getActivitiesByActivityId)

        // The @routeComponent that we added in layout.html earlier will be replaced
        // by the String inside the VueComponent. This means a call to / will load
        // the layout and display our <home-page> component.
        config.routes.get("/", VueComponent("<home-page></home-page>"))
        config.routes.get("/users", VueComponent("<user-overview></user-overview>"))
        config.routes.get("/users/{user-id}", VueComponent("<user-profile></user-profile>"))
        config.routes.get("/users/{user-id}/activities", VueComponent("<user-activity-overview></user-activity-overview>"))
    }

    private fun getRemoteAssignedPort(): Int {
        val remotePort = System.getenv("PORT")
        return if (remotePort != null) {
            Integer.parseInt(remotePort)
        } else 8080
    }

}
