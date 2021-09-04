package net.johanbasson.drive.workspace.endpoints

import arrow.core.*
import arrow.core.extensions.fx
import net.johanbasson.drive.ApiError
import net.johanbasson.drive.Environment
import net.johanbasson.drive.user.Principal
import net.johanbasson.drive.user.Role
import net.johanbasson.drive.user.authorize
import net.johanbasson.drive.user.checkPermissions
import net.johanbasson.drive.workspace.DBGetWorkspace
import net.johanbasson.drive.workspace.DBPersistNewWorkspace
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import java.util.*
import net.johanbasson.drive.ApplicationJackson.auto
import net.johanbasson.drive.badRequest
import net.johanbasson.drive.ok
import net.johanbasson.drive.workspace.Workspace

data class CreateWorkspaceRequest(val name: String, val description: String)

object CreateWorkspace {

    private val requestLens = Body.auto<CreateWorkspaceRequest>().toLens()
    private val workspaceLens = Body.auto<Workspace>().toLens()

    operator fun invoke(env: Environment, getWorkspace: DBGetWorkspace, persistNewWorkspace: DBPersistNewWorkspace): HttpHandler = {
        authorize(it)
            .flatMap { principal -> createWorkspace(env, principal, requestLens(it), getWorkspace, persistNewWorkspace) }
            .fold(
                { err -> badRequest(err) },
                { ws -> ok(ws, workspaceLens) }
            )
    }


    private fun createWorkspace(
        env: Environment,
        principal: Principal,
        request: CreateWorkspaceRequest,
        getWorkspace: DBGetWorkspace,
        persistNewWorkspace: DBPersistNewWorkspace
    ): Either<ApiError, Workspace> = Either.fx {
        val checkedPrincipal = !checkPermissions(principal, Role.USER)
        val existingWs = !getWorkspace(env.sql2o, principal, request.name)
        val checkedNonExistWs = !ensureWorkspaceDoesNotExist(existingWs)
        val newWs = Workspace(UUID.randomUUID(), request.name, request.description, Date(), Date())
        val savedWs = !persistNewWorkspace(env.sql2o, checkedPrincipal, newWs)
        savedWs
    }

    private fun ensureWorkspaceDoesNotExist(maybeWs: Option<Workspace>): Either<ApiError, Boolean> {
        return when (maybeWs) {
            is Some -> ApiError.WorkspaceAlreadyExists.left()
            is None -> true.right()
        }
    }
}