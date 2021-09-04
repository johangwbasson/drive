package net.johanbasson.drive.user

import arrow.core.Either
import arrow.core.right
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import net.johanbasson.drive.ApiError
import java.util.*
import javax.crypto.SecretKey

data class Principal(val userId: UUID, val roles: Set<Role>)
data class Token(val token: String)

object Jwt {
    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    fun generateToken(user: User): Token {
        return Token(
            Jwts.builder()
                .setId(user.id.toString())
                .setSubject(user.roles.toString())
                .signWith(secretKey)
                .compact()
        )
    }

    fun parseToken(token: String): Either<ApiError, Principal> {
        val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
        return Principal( UUID.fromString(claims.body.id), parseRoles(claims.body.subject)).right()
    }

    private fun parseRoles(roles: String): Set<Role> {
        return roles.substring(1, roles.length - 1)
            .split(",")
            .map { r -> Role.valueOf(r.trim()) }
            .toSet()
    }
}