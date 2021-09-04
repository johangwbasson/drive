package net.johanbasson.drive.workspace.endpoints

import arrow.core.Either
import arrow.core.extensions.fx
import net.johanbasson.drive.ApiError
import net.johanbasson.drive.Environment
import net.johanbasson.drive.ApplicationJackson.auto
import net.johanbasson.drive.badRequest
import net.johanbasson.drive.storage.GetFileStream
import net.johanbasson.drive.toEither
import net.johanbasson.drive.user.authorize
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import java.io.InputStream
import java.util.*

data class DownloadFileRequest(val file: UUID)

object DownloadFile {

    private val requestLens = Body.auto<DownloadFileRequest>().toLens().toEither()

    operator fun invoke(env: Environment, getFileStream: GetFileStream): HttpHandler = {
        val result: Either<ApiError, InputStream> = Either.fx {
            val principal = !authorize(it)
            val request = !requestLens(it)
            val stream = !getFileStream(env.config.fileStoreConfig, principal, request.file)
            stream
        }

        result.fold(
            { err -> badRequest(err) },
            { inp -> Response(Status.OK).body(inp) }
        )
    }
}