package net.johanbasson.drive

import arrow.core.*
import net.johanbasson.drive.ApplicationJackson.auto
import org.http4k.core.Body
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.LensExtractor
import org.http4k.lens.LensFailure
import java.util.*

fun <A> Either<Throwable, A>.unsafeCatch(f: () -> A) =
    try { f().right() } catch (t: Throwable) { t.left() }


sealed class ApiError(val errors: List<String>) {
    class ParseFailure(private val failure: LensFailure) : ApiError(failure.failures.map { it -> "${it.type} ${it.meta.name}" })
    object InvalidAuthorizationHeader : ApiError(listOf(""))
    object InvalidJWTToken : ApiError(listOf(""))
    object InsufficientPrivileges : ApiError(listOf(""))
    object AuthenticationError: ApiError(listOf(""))
    object BadCredentials: ApiError(listOf(""))
    object WorkspaceAlreadyExists: ApiError(listOf(""))
    object FolderNotFound : ApiError(listOf(""))
    object FileNotFound : ApiError(listOf(""))
    class StorageError(message: String): ApiError(listOf(message))
    class SearchError(message: String): ApiError(listOf(message))
    class IndexError(message: String): ApiError(listOf(message))
    class DatabaseError(error: String): ApiError(listOf(error))
}

fun <IN, OUT> LensExtractor<IN, OUT>.toEither(): LensExtractor<IN, Either<ApiError, OUT>> = object :
    LensExtractor<IN, Either<ApiError, OUT>> {
    override fun invoke(target: IN): Either<ApiError, OUT> = try {
        Right(this@toEither.invoke(target))
    } catch (e: LensFailure) {
        Left(ApiError.ParseFailure(e))
    }
}

val apiErrorLens = Body.auto<ApiError>().toLens()

fun badRequest(error: ApiError): Response = apiErrorLens.inject(error, Response(Status.BAD_REQUEST))

fun <T> ok(obj: T, lens: BiDiBodyLens<T>): Response {
    return lens.inject(obj, Response(Status.OK))
}

fun created(url: String): Response {
    return Response(Status.CREATED).header("Location", url)
}

fun accepted(): Response {
    return Response(Status.ACCEPTED)
}

fun deleted(id: UUID): Response {
    return Response(Status.ACCEPTED)
}