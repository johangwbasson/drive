package net.johanbasson.drive.search

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.michaelbull.logging.InlineLogger
import net.johanbasson.drive.ApiError
import net.johanbasson.drive.Environment
import net.johanbasson.drive.config.IndexStoreConfig
import net.johanbasson.drive.config.StagingStore
import net.johanbasson.drive.indexing.Index
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

suspend fun indexer(env: Environment) {
    val logger = InlineLogger("indexer")
    logger.info { "Indexer online" }
    for (fileId in env.indexChannel) {
        logger.info { "Received request : $fileId" }
        addIndex(env.config.indexStoreConfig, env.config.stagingStore, fileId)
            .fold(
                { err -> logger.error { "Error adding to index: ${err.errors}" } },
                { num -> logger.info { "Successfully added to index. Seq: $num" } }
            )
    }
}

private fun addIndex(config: IndexStoreConfig, stagingStore: StagingStore, fileId: UUID): Either<ApiError, Long> {
    val logger = InlineLogger("indexer")
    return try {
        logger.info { "Loading metadata $fileId" }
        // Refactor
        val metaFile = Paths.get(stagingStore.path, "$fileId.json")
        val index = jacksonObjectMapper().readValue<Index>(metaFile.toFile(), Index::class.java)

        logger.info { "Metadata loaded for $fileId" }

        val path = Paths.get(config.path)
        if (!Files.exists(path)) {
            Files.createDirectories(path)
        }

        logger.info { "Opening search index $fileId" }
        val dir: Directory = FSDirectory.open(path)
        val analyzer = StandardAnalyzer()

        logger.info { "Creating document $fileId" }
        val doc = Document()
        doc.add(StringField("id", index.fileId.toString(), Field.Store.YES))
        doc.add(TextField("content", index.content, Field.Store.YES))
        doc.add(StringField("principal", index.principal.toString(), Field.Store.YES))
        doc.add(StringField("size", index.size.toString(10), Field.Store.YES))
        doc.add(StringField("content-type", index.contentType, Field.Store.YES))
        doc.add(StringField("folder", index.folder.toString(), Field.Store.YES))
        doc.add(StringField("title", index.title, Field.Store.YES))

        logger.info { "Document created $fileId" }

        val iwc = IndexWriterConfig(analyzer)
        iwc.openMode = IndexWriterConfig.OpenMode.CREATE_OR_APPEND
        val writer = IndexWriter(dir, iwc)
        val id = writer.addDocument(doc)
        writer.commit()
        writer.close()

        logger.info { "Entry added into search index:  $fileId" }

        id.right()
    } catch (ex: Exception) {
        logger.error(ex) { "Unable to add document to index" }
        ApiError.IndexError(ex.localizedMessage).left()
    }
}