package net.johanbasson.drive

import com.rabbitmq.client.ConnectionFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import net.johanbasson.drive.config.*
import net.johanbasson.drive.indexing.extract
import net.johanbasson.drive.search.indexer
import org.flywaydb.core.Flyway
import org.http4k.server.ApacheServer
import org.http4k.server.asServer
import org.sql2o.Sql2o
import javax.sql.DataSource

fun main() {

    val config = Configuration(
        DBConfig("jdbc:mysql://localhost/drive", "root", "Nie0zief", "com.mysql.cj.jdbc.Driver"),
        ServerConfig(7123),
        FileStoreConfig("./files"),
        IndexStoreConfig("./indexes"),
        StagingStore("./temp")
    )

    val dataSource = createDatasource(config)
    migrateSchema(dataSource)

    val env = Environment(config, Sql2o(dataSource), Channel(), Channel(), ConnectionFactory().newConnection())

    GlobalScope.launch { extract(env) }
    GlobalScope.launch { indexer(env) }

    Server(env).asServer(ApacheServer(env.config.server.port)).start()
}

private fun createDatasource(config: Configuration): DataSource {
    val hikariConfig = HikariConfig()
    hikariConfig.driverClassName = config.database.driver
    hikariConfig.jdbcUrl = config.database.url
    hikariConfig.username = config.database.username
    hikariConfig.password = config.database.password
    return HikariDataSource(hikariConfig)
}

private fun migrateSchema(dataSource: DataSource): Int {
    return Flyway.configure().dataSource(dataSource).load().migrate()
}