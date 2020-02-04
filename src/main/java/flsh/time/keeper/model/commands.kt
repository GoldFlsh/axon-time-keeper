package flsh.time.keeper.model

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.Instant

data class OnboardNewEmployeeCommand(@TargetAggregateIdentifier val employeeName: String)

data class ClockInCommand(@TargetAggregateIdentifier val employeeName: String,
                          val timeCardEntryId: String)

data class ClockOutCommand(@TargetAggregateIdentifier val employeeName: String,
                           val timeCardEntryId: String)

data class FixTimeCardEntryCommand(
        @TargetAggregateIdentifier val employeeName: String,
        val timeCardEntryId: String,
        val startTime: Instant,
        val endTime: Instant)
