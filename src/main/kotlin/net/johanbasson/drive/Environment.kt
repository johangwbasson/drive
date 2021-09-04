package net.johanbasson.drive

import com.rabbitmq.client.Connection
import kotlinx.coroutines.channels.Channel
import net.johanbasson.drive.config.Configuration
import net.johanbasson.drive.indexing.ExtractFileRequest
import org.sql2o.Sql2o
import java.util.*

data class Environment(
    val config: Configuration,
    val sql2o: Sql2o,
    val extractChannel: Channel<ExtractFileRequest>,
    val indexChannel: Channel<UUID>,
    val rabbitMQConnection: Connection
)