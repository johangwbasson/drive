package net.johanbasson.drive.workspace.endpoints

import arrow.core.Either
import arrow.core.extensions.fx
import net.johanbasson.drive.*
import net.johanbasson.drive.ApplicationJackson.auto
import net.johanbasson.drive.user.Role
import net.johanbasson.drive.user.authorize
import net.johanbasson.drive.user.checkPermissions
import net.johanbasson.drive.workspace.Folder
import net.johanbasson.drive.workspace.FolderResource
import net.johanbasson.drive.workspace.PersistNewFolder
import net.johanbasson.drive.workspace.toResource
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.lens.Path
import org.http4k.lens.string
import java.util.*

data class CreateFolderRequest(val name: String, val description: String, val folder: UUID)

object CreateFolder {

    private val requestLens = Body.auto<CreateFolderRequest>().toLens().toEither()
    private val folderResourceLens = Body.auto<FolderResource>().toLens()
    private val idLens = Path.string().of("id").toEither()

    operator fun invoke(env: Environment, persistNewFolder: PersistNewFolder): HttpHandler = {
        val result: Either<ApiError, Folder> = Either.fx {
            val request = !requestLens(it)
            val principal = !authorize(it)
            val checkedPerms = !checkPermissions(principal, Role.USER)
            val id = !idLens(it)
            val newFolder = Folder(UUID.randomUUID(), UUID.fromString(id), request.name, request.description, Date(), Date())
            val savedFolder = !persistNewFolder(env.sql2o, principal, newFolder)
            savedFolder
        }

        result.fold(
            { err -> badRequest(err) },
            { folder -> ok(folder.toResource(), folderResourceLens) }
        )
    }
}