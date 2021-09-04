package net.johanbasson.drive.user

import arrow.core.Either
import arrow.core.Option
import net.johanbasson.drive.ApiError
import org.sql2o.Sql2o

typealias GetUserByEmail = (sql2o: Sql2o, email: String) -> Either<ApiError, Option<User>>