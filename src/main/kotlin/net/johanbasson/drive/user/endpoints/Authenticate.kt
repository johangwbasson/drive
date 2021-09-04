package net.johanbasson.drive.user.endpoints

import arrow.core.Either
import arrow.core.Option
import arrow.core.extensions.fx
import arrow.core.left
import arrow.core.right
import net.johanbasson.drive.ApiError
import net.johanbasson.drive.ApplicationJackson.auto
import net.johanbasson.drive.Environment
import net.johanbasson.drive.badRequest
import net.johanbasson.drive.ok
import net.johanbasson.drive.user.*
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.mindrot.jbcrypt.BCrypt

data class AuthenticateCommand(val email: String, val password: String)

object Authenticate {
    private val authenticateRequestLens = Body.auto<AuthenticateCommand>().toLens()
    private val tokenLens = Body.auto<Token>().toLens()

    operator fun invoke(env: Environment, getUserByEmail: GetUserByEmail): HttpHandler = {
        authenticate(env, authenticateRequestLens(it), getUserByEmail)
            .fold(
                { err -> badRequest(err) },
                { token -> ok(Token(token.token), tokenLens) }
            )
    }



    private fun authenticate(env: Environment, cmd: AuthenticateCommand, getUserByEmail: GetUserByEmail): Either<ApiError, Token> = Either.fx {
        val mayBeUser = !getUserByEmail(env.sql2o, cmd.email)
        val user: User = !ensureUserExists(mayBeUser)
        val validUser = !validatePassword(user, cmd.password)
        Jwt.generateToken(validUser)
    }

    private fun ensureUserExists(mayBeUser: Option<User>): Either<ApiError, User> {
        return mayBeUser.fold(
            { ApiError.BadCredentials.left() },
            { user -> user.right() }
        )
    }

    private fun validatePassword(user: User, plain: String): Either<ApiError, User> {
        return if (BCrypt.checkpw(plain, user.hash)) {
            user.right()
        } else {
            ApiError.BadCredentials.left()
        }
    }
}