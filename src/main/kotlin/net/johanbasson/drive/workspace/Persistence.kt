package net.johanbasson.drive.workspace

import arrow.core.*
import com.github.michaelbull.logging.InlineLogger
import net.johanbasson.drive.ApiError
import net.johanbasson.drive.user.Principal
import org.sql2o.Sql2o
import java.sql.SQLException
import java.util.*

fun getWorkspaces(sql2o: Sql2o, principal: Principal): Either<ApiError, List<Workspace>> {
    return try {
        sql2o.open().use { con ->
            con.createQuery("SELECT BIN_TO_UUID(id) AS id, name, description, created FROM workspaces WHERE user_id = :userId ORDER BY name")
                .addParameter("userId", principal.userId)
                .executeAndFetch(Workspace::class.java)
        }.right()
    } catch (ex: SQLException) {
        InlineLogger().error(ex) { "Database error" }
        ApiError.DatabaseError(ex.localizedMessage).left()
    }
}

fun getWorkspace(sql2o: Sql2o, principal: Principal, name: String): Either<ApiError, Option<Workspace>> {
    return arrow.core.None.right()
}

fun persistNewWorkspace(sql2o: Sql2o,principal: Principal, workspace: Workspace): Either<ApiError, Workspace> {
    return workspace.right()
}

fun listContents(sql2o: Sql2o, principal: Principal, id: UUID): Either<ApiError, List<Node>> {
    return listOf<Node>().right()
}

fun persistNewFolder(sql2o: Sql2o, principal: Principal, folder: Folder): Either<ApiError, Folder> {
    return folder.right()
}

fun getFolderById(sql2o: Sql2o, principal: Principal, id: UUID): Either<ApiError, Option<Folder>> {
    return None.right()
}

fun persistFile(sql2o: Sql2o, principal: Principal, file: File): Either<ApiError, File> {
    return ApiError.DatabaseError("").left()
}

fun getFile(sql2o: Sql2o, principal: Principal, id: UUID): Either<ApiError, Option<File>> {
    return None.right()
}

fun deleteFile(sql2o: Sql2o, principal: Principal, id: UUID): Either<ApiError, UUID> {
   return id.right()
}