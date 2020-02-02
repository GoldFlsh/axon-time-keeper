package flsh.time.keeper.model

import java.time.Instant

data class ClockInEvent(
        val employeeName: String,
        val time: Instant,
        val timeCardEntryId: String)

data class ClockOutEvent(
        val employeeName: String,
        val time: Instant,
        val timeCardEntryId: String)