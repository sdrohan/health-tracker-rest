package ie.setu.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.postgresql.util.PSQLException

class DbConfig {

    private val logger = KotlinLogging.logger {}
    private lateinit var dbConfig: Database

    fun getDbConnection(): Database {

        //Moving to openshift (will replace with secrets later)
        //val PGHOST = "postgresql.agile-software-dev.svc.cluster.local"
        //val PGPORT = "5432"
        //val PGUSER = "user3PN"
        //val PGPASSWORD = "06B1xJPBUALHFLPLF"
        //val PGDATABASE = "sampledb"

        // picking up environment secrets from openshift
        //val PGHOST = (System.getenv("POSTGRESQL_HOST") ?: "localhost").removePrefix("tcp://")
        val rawHost = System.getenv("POSTGRESQL_HOST") ?: "localhost"
        val PGHOST = rawHost.substringAfterLast("://")
        val PGPORT = System.getenv("POSTGRESQL_PORT") ?: "5432"
        val PGDATABASE = System.getenv("POSTGRESQL_DATABASE")  ?: "defaultdb"
        val PGUSER = System.getenv("POSTGRESQL_USER") ?: "defaultuser"
        val PGPASSWORD = System.getenv("POSTGRESQL_PASSWORD") ?: "defaultpass"

        //url format should be jdbc:postgresql://host:port/database
        val dbUrl = "jdbc:postgresql://$PGHOST:$PGPORT/$PGDATABASE"

        try {
            logger.info { "Starting DB Connection...$dbUrl \n rawHost: ${rawHost}" }
            dbConfig = Database.connect(
                url = dbUrl, driver = "org.postgresql.Driver",
                user = PGUSER, password = PGPASSWORD
            )
            logger.info { "DB Connected Successfully..." + dbConfig.url }
        } catch (e: PSQLException) {
            logger.info { "Error in DB Connection...${e.printStackTrace()}" }
            logger.info { "Env vars: $PGHOST, $PGPORT, $PGUSER, $PGDATABASE" }
        }

        return dbConfig

    }
}