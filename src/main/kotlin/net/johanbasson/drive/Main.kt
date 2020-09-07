package net.johanbasson.drive

import org.http4k.server.ApacheServer
import org.http4k.server.asServer

fun main() {
    val env = Environment(7123)
    Server(env).asServer(ApacheServer(env.port)).start()
}