package net.johanbasson.drive.indexing

import arrow.core.*
import arrow.core.extensions.fx
import arrow.core.extensions.option.foldable.size
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.johanbasson.drive.ApiError
import net.johanbasson.drive.Environment
import net.johanbasson.drive.storage.GetFileStream
import net.johanbasson.drive.storage.getFileStream
import net.johanbasson.drive.user.Principal
import net.johanbasson.drive.workspace.File
import net.johanbasson.drive.workspace.GetFile
import net.johanbasson.drive.workspace.getFile
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.sax.BodyContentHandler
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

suspend fun extract(env: Environment) {
    val logger = InlineLogger("extractor")
    logger.info { "Extractor online" }
    for (req in env.extractChannel) {
        logger.info { "Received request: $req" }
        extractText(env, req.principal, req.id, ::getFile, ::getFileStream)
            .fold(
                { err -> logger.error { err.toString() } },
                { fileId ->
                    GlobalScope.launch {
                        logger.info { "Notifying indexer: $req" }
                        env.indexChannel.send(fileId)
                    }
                }
            )
    }
}

fun extractText(
    env: Environment,
    principal: Principal,
    fileId: UUID,
    getFile: GetFile,
    getFileStream: GetFileStream
): Either<ApiError, UUID> = Either.fx {

    val handler = BodyContentHandler(-1)
    val metadata = Metadata()
    val maybeFile = !getFile(env.sql2o, principal, fileId)
    val file = !ensureFileExists(maybeFile)

    val inp = !getFileStream(env.config.fileStoreConfig, principal, fileId)
    AutoDetectParser().parse(inp, handler, metadata)

    val staging = Paths.get(env.config.stagingStore.path)
    if (!Files.exists(staging)) {
        Files.createDirectories(staging)
    }

    val metaFile = Paths.get(env.config.stagingStore.path, "$fileId.json")
    jacksonObjectMapper().writeValue(
        metaFile.toFile(),
        Index(
            principal.userId,
            fileId,
            handler.toString(),
            metadata[Metadata.CONTENT_TYPE],
            file.size,
            file.folder,
            file.name
        )
    )
    fileId
}

private fun ensureFileExists(maybeFile: Option<File>): Either<ApiError, File> {
    return when (maybeFile) {
        is Some -> maybeFile.t.right()
        is None -> ApiError.FileNotFound.left()
    }
}