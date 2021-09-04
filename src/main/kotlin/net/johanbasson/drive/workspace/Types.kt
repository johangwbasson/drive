package net.johanbasson.drive.workspace

import java.util.*

enum class NodeType {
    FILE,
    FOLDER,
    WORKSPACE
}

data class Workspace(
    val id: UUID,
    val name: String,
    val description: String,
    val created: Date,
    val modified: Date
)

data class Folder(
    val id: UUID,
    val folder: UUID,
    val name: String,
    val description: String,
    val created: Date,
    val modified: Date
)

data class File(
    val id: UUID,
    val folder: UUID,
    val name: String,
    val description: String,
    val created: Date,
    val modified: Date,
    val contentType: String,
    val size: Long
)

data class Node(
    val id: UUID,
    val nodeType: NodeType,
    val name: String,
    val description: String,
    val contentType: String,
    val size: Long,
    val modified: Date,
    val created: Date
)


data class WorkspaceResource(
    val id: UUID,
    val name: String,
    val description: String,
    val created: Date,
    val modified: Date
)

data class FolderResource(
    val id: UUID,
    val folder: UUID,
    val name: String,
    val description: String,
    val created: Date,
    val modified: Date
)

data class FileResource(
    val id: UUID,
    val folder: UUID,
    val name: String,
    val description: String,
    val created: Date,
    val modified: Date,
    val contentType: String,
    val size: Long
)

fun Workspace.toResource() = WorkspaceResource(this.id, this.name, this.description, this.created, this.modified)
fun Folder.toResource() = FolderResource(this.id, this.folder, this.name, this.description, this.created, this.modified)
fun File.toResource() = FileResource(this.id, this.folder, this.name, this.description, this.created, this.modified, this.contentType, this.size)