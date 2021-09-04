package net.johanbasson.drive.indexing

import net.johanbasson.drive.user.Principal
import java.util.*

data class ExtractFileRequest(val principal: Principal, val id: UUID)