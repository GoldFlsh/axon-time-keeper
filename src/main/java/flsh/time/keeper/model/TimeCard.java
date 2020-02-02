package flsh.time.keeper.model;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@Slf4j
public class TimeCard {

  @AggregateIdentifier
  private String employeeName;
  private Instant clockInTime;

  public TimeCard() {
    log.debug("empty constructor invoked");
  }

  @CommandHandler
  public TimeCard(ClockInCommand cmd) {
    AggregateLifecycle.apply(new ClockInEvent(cmd.getEmployeeName(), GenericEventMessage.clock.instant()));
  }

  @EventSourcingHandler
  public void on(ClockInEvent event) {
    this.employeeName = event.getEmployeeName();
    this.clockInTime = event.getClockInTime();
  }
}
