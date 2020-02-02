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
    String randomUUID = UUID.randomUUID().toString();
    testFixture.givenNoPriorActivity()
        .when(new ClockInCommand(employeeName, randomUUID))
        .expectEvents(new ClockInEvent(employeeName, testFixture.currentTime(), randomUUID));
  }

  @ParameterizedTest
  @MethodSource(value = "randomEmployeeName")
  void testClockOutCommand(String employeeName) {
    String randomUUID = UUID.randomUUID().toString();
    testFixture
        .givenCommands(new ClockInCommand(employeeName, randomUUID))
        .when(new ClockOutCommand(employeeName, randomUUID))
        .expectEvents(new ClockOutEvent(employeeName, testFixture.currentTime(), randomUUID));
  }

  private static Stream<String> randomEmployeeName() {
    return Stream.generate(RandomString::make).limit(10);
  }
}
