package commands;

import control.dto.Pose;
import org.junit.Before;

import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/** @author Hoang Tung Dinh */
public class MoveToPoseBuilderTest extends AbstractBuilderTest {

  private Pose goalPose;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    goalPose = mock(Pose.class, RETURNS_MOCKS);
  }

  @Override
  void createAndExecuteCommand(ArgumentHolder argumentHolder) {
    final Command moveToPose =
        MoveToPose.builder()
            .withVelocityService(argumentHolder.velocityService())
            .withStateEstimator(argumentHolder.stateEstimator())
            .withGoalPose(goalPose)
            .withDurationInSeconds(argumentHolder.durationInSeconds())
            .withPidLinearXParameters(argumentHolder.pidLinearX())
            .withPidLinearYParameters(argumentHolder.pidLinearY())
            .withPidLinearZParameters(argumentHolder.pidLinearZ())
            .withPidAngularZParameters(argumentHolder.pidAngularZ())
            .build();

    moveToPose.execute();
  }

  @Override
  void checkCorrectExtraMethodsCalled() {
    verify(goalPose, atLeastOnce()).x();
    verify(goalPose, atLeastOnce()).y();
    verify(goalPose, atLeastOnce()).z();
    verify(goalPose, atLeastOnce()).yaw();
  }
}
