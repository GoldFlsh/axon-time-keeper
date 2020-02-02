package flsh.time.keeper.model

import org.axonframework.modelling.command.TargetAggregateIdentifier

data class ClockInCommand(@TargetAggregateIdentifier val employeeName: String)