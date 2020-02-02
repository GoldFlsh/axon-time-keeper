package flsh.time.keeper.model;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.modelling.command.EntityId;
import org.axonframework.modelling.command.ForwardMatchingInstances;
import org.axonframework.spring.stereotype.Aggregate;

@Slf4j
@Aggregate
@SuppressWarnings("unused")
public class TimeCard {

  @AggregateIdentifier
  private String employeeName;

  @AggregateMember(eventForwardingMode = ForwardMatchingInstances.class)
  private List<TimeCardEntry> timeCardEntries = new ArrayList<>();

  private TimeCard() {
    //Empty Constructor for Axon framework
  }

  @CommandHandler
  public TimeCard(OnboardNewEmployeeCommand cmd) {
    AggregateLifecycle.apply(new EmployeeOnboardedEvent(cmd.getEmployeeName()));
  }

  @CommandHandler
  public void handle(ClockInCommand cmd) {
    apply(new ClockedInEvent(cmd.getEmployeeName(),
        GenericEventMessage.clock.instant(),
        cmd.getTimeCardEntryId()));
  }

  @CommandHandler
  public void handle(ClockOutCommand cmd) {
    getEntryIfOpen(cmd.getTimeCardEntryId()).
        ifPresentOrElse(
            entry -> apply(
                new ClockedOutEvent(cmd.getEmployeeName(),
                    GenericEventMessage.clock.instant(),
                    entry.getTimeCardEntryId())),
            () -> log.error("Employee has not clocked in or is already clocked out"));
  }

  @EventSourcingHandler
  public void on(EmployeeOnboardedEvent event) {
    this.employeeName = event.getEmployeeName();
  }

  @EventSourcingHandler
  public void on(ClockedInEvent event) {
    timeCardEntries.add(new TimeCardEntry(event.getTimeCardEntryId(), event.getTime()));
  }

  private Optional<TimeCardEntry> getEntryIfOpen(String timeCardEntryId) {
    return timeCardEntries.stream()
        .filter(entry -> entry.getTimeCardEntryId().equals(timeCardEntryId))
        .filter(TimeCardEntry::isClockedIn)
        .findFirst();
  }

  @Getter
  @Data
  public class TimeCardEntry {

    @EntityId
    private final String timeCardEntryId;
    private Instant clockInTime;
    private Instant clockOutTime;

    public TimeCardEntry(String timeCardEntryId, Instant clockInTime) {
      this.timeCardEntryId = timeCardEntryId;
      this.clockInTime = clockInTime;
    }

    @CommandHandler
    public void handle(UpdateTimeCardEntryCommand cmd) {
      if (cmd.getTimeCardEntryId().equals(timeCardEntryId)) {
        apply(new TimeCardUpdatedEvent(cmd.getEmployeeName(), cmd.getTimeCardEntryId(),
            cmd.getStartTime(), cmd.getEndTime()));
      }
    }

    @EventSourcingHandler
    public void on(ClockedOutEvent event) {
      this.clockOutTime = event.getTime();
    }

    @EventSourcingHandler
    public void on(TimeCardUpdatedEvent event) {
      this.clockInTime = event.getStartTime();
      this.clockOutTime = event.getEndTime();
    }

    private boolean isClockedIn() {
      return clockOutTime == null;
    }
  }
}