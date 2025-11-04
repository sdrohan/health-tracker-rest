package ie.setu.helpers

import ie.setu.domain.db.Users
import ie.setu.domain.db.Activities
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object TestDatabaseConfig {

    private var initialised = false

    fun connect() {
        if (!initialised) {
            Database.connect(
                url = "jdbc:h2:mem:test-db;DB_CLOSE_DELAY=-1",
                driver = "org.h2.Driver",
                user = "sa",
                password = ""
            )
            initialised = true

            transaction {
                SchemaUtils.create(Users, Activities)
            }
        }
    }

    fun reset() {
        transaction {
            SchemaUtils.drop(Users, Activities)
            SchemaUtils.create(Users, Activities)
        }
    }
}
