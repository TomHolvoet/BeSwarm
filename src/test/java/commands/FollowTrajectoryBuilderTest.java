package commands;

import commands.bebopcommands.BebopFollowTrajectory;
import control.VelocityController4d;
import control.localization.StateEstimator;
import org.junit.Test;
import services.Velocity4dService;

import static org.mockito.Mockito.mock;

/**
 * Tests for {@link BebopFollowTrajectory}'s builder.
 *
 * @author Hoang Tung Dinh
 */
public class FollowTrajectoryBuilderTest extends AbstractBuilderTest {

  @Override
  void createAndExecuteCommand(ArgumentHolder argumentHolder) {
    final Command followTrajectory =
        BebopFollowTrajectory.builder()
            .withVelocity4dService(argumentHolder.velocityService())
            .withStateEstimator(argumentHolder.stateEstimator())
            .withTimeProvider(argumentHolder.timeProvider())
            .withDurationInSeconds(argumentHolder.durationInSeconds())
            .withVelocityController4d(argumentHolder.velocityController4d())
            .build();

    followTrajectory.execute();
  }

  @Test(expected = NullPointerException.class)
  public void testMissingTimeProvider() {
    BebopFollowTrajectory.builder()
        .withVelocity4dService(mock(Velocity4dService.class))
        .withStateEstimator(mock(StateEstimator.class))
        .withDurationInSeconds(1)
        .withVelocityController4d(mock(VelocityController4d.class))
        .build();
  }
}
