package flsh.time.keeper.model;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.Test;

class TimeCardTest {

  AggregateTestFixture<TimeCard> testFixture = new AggregateTestFixture<>(TimeCard.class);
  Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

  @Test
  void testClockInCommand() {
    testFixture.givenNoPriorActivity()
        .andGivenCurrentTime(clock.instant())
        .when(new ClockInCommand("GoldFlsh"))
        .expectEvents(new ClockInEvent("GoldFlsh", testFixture.currentTime()));
  }

}
