package ie.setu.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.postgresql.util.PSQLException

class DbConfig {

    private val logger = KotlinLogging.logger {}
    private lateinit var dbConfig: Database

    fun getDbConnection(): Database {

        //val PGHOST = (System.getenv("POSTGRESQL_HOST") ?: "localhost").removePrefix("tcp://")
        val rawHost = System.getenv("POSTGRESQL_HOST") ?: "localhost"
        val PGHOST = rawHost.substringAfterLast("://")
        val PGPORT = System.getenv("POSTGRESQL_SERVICE_PORT") ?: "5432"
        val PGDATABASE = System.getenv("POSTGRESQL_DATABASE") ?: "defaultdb"
        val PGUSER = System.getenv("POSTGRESQL_USER") ?: "defaultuser"
        val PGPASSWORD = System.getenv("POSTGRESQL_PASSWORD") ?: "defaultpass"


        // JDBC connection string format
        //val dbUrl = "jdbc:postgresql://$PGHOST:$PGPORT/$PGDATABASE"
        val dbUrl = "jdbc:postgresql://$rawHost:$PGPORT/$PGDATABASE"

        try {
            logger.info { "Starting DB Connection...\nURL: $dbUrl" }
            logger.info { "rawHost...version2: $rawHost" }
            logger.info { "pghost...version2: $PGHOST" }
            dbConfig = Database.connect(
                url = dbUrl,
                driver = "org.postgresql.Driver",
                user = PGUSER,
                password = PGPASSWORD
            )
            logger.info { "DB Connected Successfully to $PGDATABASE at $rawHost:$PGPORT" }
        } catch (e: PSQLException) {
            logger.error(e) { "Error in DB Connection: ${e.message}" }
            logger.info { "Env vars used: host=$PGHOST, port=$PGPORT, user=$PGUSER, db=$PGDATABASE" }
        }

        return dbConfig
    }
}
