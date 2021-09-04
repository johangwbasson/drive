package net.johanbasson.drive.workspace.endpoints

import arrow.core.*
import arrow.core.extensions.fx
import net.johanbasson.drive.*
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import net.johanbasson.drive.ApplicationJackson.auto
import net.johanbasson.drive.storage.DeleteFileFromStore
import net.johanbasson.drive.user.authorize
import net.johanbasson.drive.workspace.DeleteFile
import net.johanbasson.drive.workspace.File
import net.johanbasson.drive.workspace.GetFile
import java.util.*

data class DeleteFileRequest(val file: UUID)

object RemoveFile {

    private val requestLens = Body.auto<DeleteFileRequest>().toLens().toEither()

    operator fun invoke(env: Environment, getFile:GetFile, removeFile: DeleteFileFromStore, deleteFile: DeleteFile): HttpHandler = {
        val result: Either<ApiError, UUID> = Either.fx {
            val principal = !authorize(it)
            val request = !requestLens(it)
            val maybeFile = !getFile(env.sql2o, principal, request.file)
            val checkFileExists = !ensureFileExists(maybeFile)
            val removed = !removeFile(env.config.fileStoreConfig, principal, request.file)
            val deleted = !deleteFile(env.sql2o, principal, request.file)
            request.file
        }

        result.fold(
            { err -> badRequest(err) },
            { id -> deleted(id) }
        )
    }

    private fun ensureFileExists(maybeFile: Option<File>): Either<ApiError, File> {
        return when (maybeFile) {
            is Some -> maybeFile.t.right()
            is None -> ApiError.FileNotFound.left()
        }
    }
}