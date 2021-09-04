package net.johanbasson.drive.storage

import arrow.core.Either
import net.johanbasson.drive.ApiError
import net.johanbasson.drive.config.FileStoreConfig
import net.johanbasson.drive.user.Principal
import java.io.InputStream
import java.nio.file.Path
import java.util.*

typealias StoreFileContent = (cfg: FileStoreConfig, principal: Principal, id: UUID, inp: InputStream) -> Either<ApiError, Path>
typealias GetFileStream = (cfg: FileStoreConfig, principal: Principal, id: UUID) -> Either<ApiError, InputStream>
typealias DeleteFileFromStore = (cfg: FileStoreConfig, principal: Principal, id: UUID) -> Either<ApiError, Boolean>