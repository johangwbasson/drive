package net.johanbasson.drive

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
                    "/health" bind Method.GET to { Response(OK) }
                )
            )
    }
}