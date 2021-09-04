package net.johanbasson.drive.storage

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.fx.IO
import com.github.michaelbull.logging.InlineLogger
import net.johanbasson.drive.ApiError
import net.johanbasson.drive.config.FileStoreConfig
import net.johanbasson.drive.user.Principal
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

private fun location(cfg: FileStoreConfig, principal: Principal, id: UUID): Path {
    val path = Paths.get(cfg.path, principal.userId.toString())
    if (!Files.exists(path)) {
        Files.createDirectories(path)
    }
    return Paths.get(cfg.path, principal.userId.toString(), id.toString())
}

fun storeFile(cfg: FileStoreConfig, principal: Principal, id: UUID, inp: InputStream): Either<ApiError, Path> {
    return try {
        val file = location(cfg, principal, id)
        BufferedOutputStream(FileOutputStream(file.toFile())).use {
            inp.copyTo(it)
        }
        file.right()
    } catch (ex: IOException) {
        InlineLogger().error(ex) { "Unable to store file"}
        ApiError.StorageError(ex.localizedMessage).left()
    }
}

fun deleteFileFromStore(cfg: FileStoreConfig, principal: Principal, id: UUID): Either<ApiError, Boolean> {
    return try {
        val file = location(cfg, principal, id)
        file.toFile().delete()
        true.right()
    } catch (ex: IOException) {
        InlineLogger().error(ex) {"Unable to delete file $id" }
        ApiError.StorageError(ex.localizedMessage).left()
    }
}

fun getFileStream(cfg: FileStoreConfig, principal: Principal, id: UUID): Either<ApiError, InputStream> {
    return try {
        val path = location(cfg, principal, id)
        return BufferedInputStream(FileInputStream(path.toFile())).right()
    } catch (ex: FileNotFoundException) {
        InlineLogger().error(ex) {"Unable to find file $id" }
        ApiError.StorageError(ex.localizedMessage).left()
    }
}
