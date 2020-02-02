package flsh.time.keeper.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.*;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.*;

@Slf4j
@Aggregate
public class TimeCard {

  @AggregateIdentifier
  private String employeeName;

  @AggregateMember(eventForwardingMode = ForwardMatchingInstances.class)
  private List<TimeCardEntry> timeCardEntries = new ArrayList<>();

  public TimeCard() {
    log.debug("empty constructor invoked");
  }

  @CommandHandler
  public TimeCard(ClockInCommand cmd) {
    apply(new ClockInEvent(cmd.getEmployeeName(),
        GenericEventMessage.clock.instant(),
        cmd.getTimeCardEntryId()));
  }

  @CommandHandler
  public void handle(ClockOutCommand cmd) {
    getEntryIfOpen(cmd.getTimeCardEntryId()).
        ifPresentOrElse(
            entry -> apply(
                new ClockOutEvent(cmd.getEmployeeName(),
                    GenericEventMessage.clock.instant(),
                    entry.timeCardEntryId)),
            () -> log.error("Employee has not clocked in"));
  }

  @EventSourcingHandler
  public void on(ClockInEvent event) {
    this.employeeName = event.getEmployeeName();
    timeCardEntries.add(new TimeCardEntry(event.getTimeCardEntryId(), event.getTime()));
  }

  private Optional<TimeCardEntry> getEntryIfOpen(String timeCardEntryId) {
    return timeCardEntries.stream()
        .filter(entry -> entry.getTimeCardEntryId().equals(timeCardEntryId))
        .filter(TimeCardEntry::isClockedIn)
        .findFirst();
  }

  @Data
  public class TimeCardEntry {

    @EntityId
    private final String timeCardEntryId;
    private final Instant clockInTime;
    private Instant clockOutTime;

    @EventSourcingHandler
    public void on(ClockOutEvent event) {
      this.clockOutTime = event.getTime();
    }

    private boolean isClockedIn() {
      return clockOutTime == null;
    }
  }
}