package net.johanbasson.drive.user

import java.util.*

data class User(
    val id: UUID,
    val email: String,
    val hash: String,
    val roles: List<String>
) { companion object }

enum class Role {
    USER,
    ADMINISTRATOR
}