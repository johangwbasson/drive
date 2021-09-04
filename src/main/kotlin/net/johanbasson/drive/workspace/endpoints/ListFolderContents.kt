package net.johanbasson.drive.workspace.endpoints

import arrow.core.Either
import arrow.core.extensions.fx
import net.johanbasson.drive.*
import net.johanbasson.drive.ApplicationJackson.auto
import net.johanbasson.drive.user.Role
import net.johanbasson.drive.user.authorize
import net.johanbasson.drive.user.checkPermissions
import net.johanbasson.drive.workspace.ListNodes
import net.johanbasson.drive.workspace.Node
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.lens.Path
import org.http4k.lens.string
import java.util.*

object ListFolderContents {

    private val idLens = Path.string().of("id").toEither()
    private val nodesLens = Body.auto<List<Node>>().toLens()

    operator fun invoke(env: Environment, listNodes: ListNodes): HttpHandler = {
        val result: Either<ApiError, List<Node>> = Either.fx {
            val id = !idLens(it)
            val principal = !authorize(it)
            val checkedPerms = !checkPermissions(principal, Role.USER)
            val contents = !listNodes(env.sql2o, principal, UUID.fromString(id))
            contents
        }

        result.fold(
            { err -> badRequest(err) },
            { contents -> ok(contents, nodesLens)}
        )
    }

}