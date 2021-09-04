package net.johanbasson.drive.workspace.endpoints

import arrow.core.*
import arrow.core.extensions.fx
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import net.johanbasson.drive.*
import net.johanbasson.drive.ApplicationJackson.auto
import net.johanbasson.drive.indexing.ExtractFileRequest
import net.johanbasson.drive.storage.StoreFileContent
import net.johanbasson.drive.user.authorize
import net.johanbasson.drive.workspace.*
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.MultipartFormBody
import org.http4k.lens.Path
import org.http4k.lens.string
import java.util.*

object CreateFile {

    private val filesLens = Body.auto<List<FileResource>>().toLens()
    private val idLens = Path.string().of("id").toEither()

    operator fun invoke(env: Environment,
                        getFolderById: GetFolderById,
                        persistFile: PersistFile,
                        storeFileContent: StoreFileContent,
                        extractChannel: Channel<ExtractFileRequest>
    ): HttpHandler = {
        val result: Either<ApiError, List<FileResource>> = Either.fx {
            val principal = !authorize(it)
            val folderId = !idLens(it)
            val maybeFolder = !getFolderById(env.sql2o, principal, UUID.fromString(folderId))
            val folder = !ensureFolderExists(maybeFolder)
            val createdFiles = ArrayList<FileResource>()
            val receivedForm = MultipartFormBody.from(it)
            receivedForm.files("file").forEach { formFile ->
                val fileId = UUID.randomUUID()
                val path = !storeFileContent(env.config.fileStoreConfig, principal, fileId, formFile.content)
                val size = path.toFile().length()
                val created = !persistFile(env.sql2o, principal, File(fileId, folder.id, formFile.filename, "", Date(), Date(), formFile.contentType.value, size))
                GlobalScope.launch {
                    extractChannel.send(ExtractFileRequest(principal, fileId))
                }
                createdFiles.add(created.toResource())
            }

            createdFiles
        }

        result
            .fold(
                { err -> badRequest(err) },
                { files -> ok(files, filesLens) }
            )
    }

    private fun ensureFolderExists(maybeFolder: Option<Folder>): Either<ApiError, Folder> {
        return when (maybeFolder) {
            is Some -> maybeFolder.t.right()
            is None -> ApiError.FolderNotFound.left()
        }
    }
}