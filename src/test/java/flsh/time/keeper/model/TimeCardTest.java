package flsh.time.keeper.model;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;
import net.bytebuddy.utility.RandomString;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class TimeCardTest {

  AggregateTestFixture<TimeCard> testFixture;

  @BeforeEach
  void setUp() {
    testFixture = new AggregateTestFixture<>(TimeCard.class);
  }

  @ParameterizedTest
  @MethodSource(value = "randomEmployeeName")
  void testOnboardNewEmployeeCommand(String employeeName) {
    testFixture.givenNoPriorActivity()
        .when(new OnboardNewEmployeeCommand(employeeName))
        .expectEvents(new EmployeeOnboardedEvent(employeeName));
  }

  @ParameterizedTest
  @MethodSource(value = "randomEmployeeName")
  void testClockInCommand(String employeeName) {
    String timeCardEntryUuid = UUID.randomUUID().toString();
    testFixture.givenCommands(new OnboardNewEmployeeCommand(employeeName))
        .when(new ClockInCommand(employeeName, timeCardEntryUuid))
        .expectEvents(
            new ClockedInEvent(employeeName, testFixture.currentTime(), timeCardEntryUuid));
  }

  @ParameterizedTest
  @MethodSource(value = "randomEmployeeName")
  void testClockOutCommand(String employeeName) {
    String timeCardEntryUuid = UUID.randomUUID().toString();
    testFixture
        .givenCommands(new OnboardNewEmployeeCommand(employeeName),
            new ClockInCommand(employeeName, timeCardEntryUuid))
        .when(new ClockOutCommand(employeeName, timeCardEntryUuid))
        .expectEvents(
            new ClockedOutEvent(employeeName, testFixture.currentTime(), timeCardEntryUuid));
  }

  @ParameterizedTest
  @MethodSource(value = "randomEmployeeName")
  void testUpdateTimeEntryCommand(String employeeName) {
    String timeCardEntryUuid = UUID.randomUUID().toString();
    testFixture.
        givenCommands(new OnboardNewEmployeeCommand(employeeName),
            new ClockInCommand(employeeName, timeCardEntryUuid))
        .andGivenCurrentTime(addDays(8))
        .andGivenCommands(new ClockOutCommand(employeeName, timeCardEntryUuid))
        .when(new UpdateTimeCardEntryCommand(
            employeeName, timeCardEntryUuid,
            subtractDays(9),
            subtractDays(1)))
        .expectEvents(new TimeCardUpdatedEvent(employeeName,
            timeCardEntryUuid,
            subtractDays(9),
            subtractDays(1)));
  }

  private Instant addDays(Integer days) {
    return testFixture.currentTime().plus(Duration.ofHours(days));
  }

  private Instant subtractDays(Integer days) {
    return testFixture.currentTime().minus(Duration.ofHours(days));
  }

  private static Stream<String> randomEmployeeName() {
    return Stream.generate(RandomString::make).limit(10);
  }
}
