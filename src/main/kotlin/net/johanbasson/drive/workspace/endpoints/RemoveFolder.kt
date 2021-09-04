package net.johanbasson.drive.workspace.endpoints

import net.johanbasson.drive.Environment
import net.johanbasson.drive.accepted
import org.http4k.core.HttpHandler

object RemoveFolder {

    operator fun invoke(env: Environment): HttpHandler = {
        // TODO Recursive delete files and folders
        accepted()
    }
}