package net.johanbasson.drive.indexing

import java.util.*


data class Index(
    val principal: UUID,
    val fileId: UUID,
    val content: String,
    val contentType: String,
    val size: Long,
    val folder: UUID,
    val title: String
)