package flsh.time.keeper.model

import java.time.Instant


data class EmployeeOnboardedEvent(val employeeName: String)

data class ClockedInEvent(
        val employeeName: String,
        val time: Instant,
        val timeCardEntryId: String)

data class ClockedOutEvent(
        val employeeName: String,
        val time: Instant,
        val timeCardEntryId: String)

data class TimeCardUpdatedEvent(
        val employeeName: String,
        val timeCardEntryId: String,
        val startTime: Instant,
        val endTime: Instant
)