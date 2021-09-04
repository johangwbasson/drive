package net.johanbasson.drive.user

import arrow.core.Either
import arrow.core.Option
import arrow.core.left
import arrow.core.right
import com.github.michaelbull.logging.InlineLogger
import net.johanbasson.drive.ApiError
import org.sql2o.ResultSetHandler
import org.sql2o.Sql2o
import java.sql.SQLException
import java.util.*


fun getUserByEmail(sql2o: Sql2o, email: String): Either<ApiError, Option<User>> {
    return try {
        Option.fromNullable(
            sql2o.open().use { con ->
                con.createQuery("SELECT id, email, hash, roles FROM users WHERE email = :email")
                    .addParameter("email", email)
                    .executeAndFetchFirst(ResultSetHandler { rs ->
                        User(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("email"),
                            rs.getString("hash"),
                            rs.getString("roles").split(",")
                        )
                    })
            }
        ).right()
    } catch (ex: SQLException) {
        InlineLogger().error(ex) { "Database error" }
        ApiError.DatabaseError(ex.localizedMessage).left()
    }
}

