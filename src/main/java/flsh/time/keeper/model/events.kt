package flsh.time.keeper.model

import java.time.Instant

data class ClockInEvent(
        val employeeName: String,
        val clockInTime: Instant)