package flsh.time.keeper.model;

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
  void testClockInCommand(String employeeName) {
    String timeCardEntryUuid = UUID.randomUUID().toString();
    testFixture.givenNoPriorActivity()
        .when(new ClockInCommand(employeeName, timeCardEntryUuid))
        .expectEvents(new ClockedInEvent(employeeName, testFixture.currentTime(), timeCardEntryUuid));
  }

  @ParameterizedTest
  @MethodSource(value = "randomEmployeeName")
  void testClockOutCommand(String employeeName) {
    String timeCardEntryUuid = UUID.randomUUID().toString();
    testFixture
        .givenCommands(new ClockInCommand(employeeName, timeCardEntryUuid))
        .when(new ClockOutCommand(employeeName, timeCardEntryUuid))
        .expectEvents(new ClockedOutEvent(employeeName, testFixture.currentTime(), timeCardEntryUuid));
  }

  private static Stream<String> randomEmployeeName() {
    return Stream.generate(RandomString::make).limit(10);
  }
}
