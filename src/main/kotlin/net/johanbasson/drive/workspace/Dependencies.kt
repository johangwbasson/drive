package net.johanbasson.drive.workspace

import arrow.core.Either
import arrow.core.Option
import net.johanbasson.drive.ApiError
import net.johanbasson.drive.user.Principal
import org.sql2o.Sql2o
import java.io.InputStream
import java.nio.file.Path
import java.util.*

typealias DBGetWorkspaces = (sql2o: Sql2o, principal: Principal) -> Either<ApiError, List<Workspace>>
typealias DBGetWorkspace = (sql2o: Sql2o, principal: Principal, name: String) -> Either<ApiError, Option<Workspace>>
typealias DBPersistNewWorkspace = (sql2o: Sql2o, principal: Principal, workspace: Workspace) -> Either<ApiError, Workspace>

typealias GetFolderById = (sql2o: Sql2o, principal: Principal, id: UUID) -> Either<ApiError, Option<Folder>>

typealias GetFolder = (sql2o: Sql2o, principal: Principal, name: String, folder: UUID) -> Either<ApiError, Option<Folder>>

typealias PersistNewFolder = (sql2o: Sql2o, principal: Principal, folder: Folder) -> Either<ApiError, Folder>

typealias ListNodes = (sql2o: Sql2o, principal: Principal, folder: UUID) -> Either<ApiError, List<Node>>

typealias PersistFile = (sql2o: Sql2o, principal: Principal, file: File) -> Either<ApiError, File>

typealias DeleteFile = (sql2o: Sql2o, principal: Principal, id: UUID) -> Either<ApiError, UUID>

typealias GetFile = (sql2o: Sql2o, principal: Principal, id: UUID) -> Either<ApiError, Option<File>>