package ie.setu.config

import ie.setu.controllers.HealthTrackerController
import ie.setu.utils.jsonObjectMapper
import io.javalin.Javalin
import io.javalin.config.JavalinConfig
import io.javalin.json.JavalinJackson
import io.javalin.plugin.bundled.JavalinVuePlugin
import io.javalin.vue.VueComponent


class ServerConfig {

    fun startJavalinService(): Javalin {

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
        }.start(getRemoteAssignedPort())

        return app
    }



    private fun registerRoutes(config: JavalinConfig) {
        config.routes.get("/api/users", HealthTrackerController::getAllUsers)
        config.routes.get("/api/users/{user-id}", HealthTrackerController::getUserByUserId)
        config.routes.post("/api/users", HealthTrackerController::addUser)
        config.routes.get("api/users/email/{email}", HealthTrackerController::getUserByEmail)
        config.routes.delete("/api/users/{user-id}", HealthTrackerController::deleteUser)
        config.routes.patch("/api/users/{user-id}", HealthTrackerController::updateUser)
        config.routes.get("/api/activities", HealthTrackerController::getAllActivities)
        config.routes.post("/api/activities", HealthTrackerController::addActivity)
        config.routes.get("/api/users/{user-id}/activities", HealthTrackerController::getActivitiesByUserId)
        config.routes.delete("/api/users/{user-id}/activities", HealthTrackerController::deleteActivityByUserId)
        config.routes.delete("/api/activities/{activity-id}", HealthTrackerController::deleteActivityByActivityId)
        config.routes.patch("/api/activities/{activity-id}", HealthTrackerController::updateActivity)
        config.routes.get("/api/activities/{activity-id}", HealthTrackerController::getActivitiesByActivityId)

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

