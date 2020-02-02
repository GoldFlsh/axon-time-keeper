package flsh.time.keeper.model

import java.time.Instant

data class ClockedInEvent(
        val employeeName: String,
        val time: Instant,
        val timeCardEntryId: String)

data class ClockedOutEvent(
        val employeeName: String,
        val time: Instant,
        val timeCardEntryId: String)