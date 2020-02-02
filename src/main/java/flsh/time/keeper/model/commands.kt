package flsh.time.keeper.model

import org.axonframework.modelling.command.TargetAggregateIdentifier

data class ClockInCommand(@TargetAggregateIdentifier val employeeName: String,
                          val timeCardEntryId: String)

data class ClockOutCommand(@TargetAggregateIdentifier val employeeName: String,
                           val timeCardEntryId: String)
