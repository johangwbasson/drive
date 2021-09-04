package net.johanbasson.drive.config

data class DBConfig(val url: String, val username: String, val password: String, val driver: String)

data class ServerConfig(val port: Int)
data class FileStoreConfig(val path: String)
data class IndexStoreConfig(val path: String)
data class StagingStore(val path: String)


data class Configuration(val database: DBConfig,
                         val server: ServerConfig,
                         val fileStoreConfig: FileStoreConfig,
                         val indexStoreConfig: IndexStoreConfig,
                         val stagingStore: StagingStore
)