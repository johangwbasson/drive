package net.johanbasson.drive

import net.johanbasson.drive.storage.deleteFileFromStore
import net.johanbasson.drive.storage.getFileStream
import net.johanbasson.drive.storage.storeFile
import net.johanbasson.drive.user.endpoints.Authenticate
import net.johanbasson.drive.user.getUserByEmail
import net.johanbasson.drive.workspace.*
import net.johanbasson.drive.workspace.endpoints.*
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

object Server {

    operator fun invoke(env: Environment): RoutingHttpHandler {
        return ServerFilters.RequestTracing()
            .then(ServerFilters.CatchAll())
            .then(ServerFilters.CatchLensFailure {
                Response(Status.BAD_REQUEST).body(it.message + "\n" + it.cause.toString())
            })
            .then(
                routes(
                    "/ping" bind Method.GET to { Response(OK).body("pong") },
                    "/health" bind Method.GET to { Response(OK) },
                    "/authenticate" bind Method.POST to Authenticate(env, ::getUserByEmail),
                    "/workspaces" bind routes (
                        "/" bind Method.GET to ListWorkspaces(env, ::getWorkspaces),
                        "/" bind Method.POST to CreateWorkspace(env, ::getWorkspace, ::persistNewWorkspace)
                    ),
                    "/folders" bind routes (
                        "/{id}" bind Method.GET to ListFolderContents(env, ::listContents),
                        "/{id}" bind Method.DELETE to RemoveFolder(env),
                        "/{id}/folder" bind Method.POST to CreateFolder(env, ::persistNewFolder),
                        "/{id}/file" bind Method.POST to CreateFile(env, ::getFolderById, ::persistFile, ::storeFile, env.extractChannel)
                    ),
                    "/files" bind routes (
                        "/{id}" bind Method.GET to DownloadFile(env, ::getFileStream),
                        "/{id}" bind Method.DELETE to RemoveFile(env, ::getFile, ::deleteFileFromStore, ::deleteFile)
                    )
                )
            )
    }
}