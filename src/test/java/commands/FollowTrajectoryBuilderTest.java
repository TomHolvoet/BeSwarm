package commands;

import control.Trajectory4d;
import org.junit.Before;
import utils.TestUtils;

import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;

/** @author Hoang Tung Dinh */
public class FollowTrajectoryBuilderTest extends AbstractBuilderTest {

  private Trajectory4d trajectory4d;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    trajectory4d = mock(Trajectory4d.class, RETURNS_MOCKS);
  }

  @Override
  void createAndExecuteCommand(ArgumentHolder argumentHolder) {
    final Command followTrajectory =
        FollowTrajectory.builder()
            .withVelocityService(argumentHolder.velocityService())
            .withStateEstimator(argumentHolder.stateEstimator())
            .withTrajectory4d(trajectory4d)
            .withDurationInSeconds(argumentHolder.durationInSeconds())
            .withPidLinearXParameters(argumentHolder.pidLinearX())
            .withPidLinearYParameters(argumentHolder.pidLinearY())
            .withPidLinearZParameters(argumentHolder.pidLinearZ())
            .withPidAngularZParameters(argumentHolder.pidAngularZ())
            .build();

    followTrajectory.execute();
  }

  @Override
  void checkCorrectExtraMethodsCalled() {
    TestUtils.verifyTrajectoryCalled(trajectory4d);
  }
}
