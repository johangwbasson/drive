package net.johanbasson.drive.workspace.endpoints

import arrow.core.flatMap
import net.johanbasson.drive.ApplicationJackson.auto
import net.johanbasson.drive.Environment
import net.johanbasson.drive.badRequest
import net.johanbasson.drive.ok
import net.johanbasson.drive.user.authorize
import net.johanbasson.drive.workspace.DBGetWorkspaces
import net.johanbasson.drive.workspace.Workspace
import org.http4k.core.Body
import org.http4k.core.HttpHandler

object ListWorkspaces {

    private val workspacesLens = Body.auto<List<Workspace>>().toLens()

    operator fun invoke(env: Environment, getWorkspaces: DBGetWorkspaces): HttpHandler = { request ->
        authorize(request)
            .flatMap { principal ->  getWorkspaces(env.sql2o, principal) }
            .fold(
                { err -> badRequest(err) },
                { workspaces -> ok(workspaces, workspacesLens) }
            )
    }

}