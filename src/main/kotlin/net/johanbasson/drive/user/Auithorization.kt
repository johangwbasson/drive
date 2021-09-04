package net.johanbasson.drive.user

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import net.johanbasson.drive.ApiError
import org.http4k.core.Request

fun authorize(request: Request): Either<ApiError, Principal> {
    try {
        val header = request.header("authorization")
        if (header == null || header.length < 7) {
            return ApiError.InvalidAuthorizationHeader.left()
        }
        val token = header.substring(7).trim()
        return Jwt.parseToken(token)
    } catch (ex: Exception) {
        ex.printStackTrace()
        return ApiError.InvalidJWTToken.left()
    }
}


fun checkPermissions(principal: Principal, role: Role): Either<ApiError, Principal> {
    return if (principal.roles.contains(role)) {
        principal.right()
    } else {
        ApiError.InsufficientPrivileges.left()
    }
}