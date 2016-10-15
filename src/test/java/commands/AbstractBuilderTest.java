package commands;

import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import control.VelocityController4d;
import control.dto.DroneStateStamped;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import localization.StateEstimator;
import org.junit.Before;
import org.junit.Test;
import org.ros.time.TimeProvider;
import services.Velocity4dService;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Hoang Tung Dinh */
public abstract class AbstractBuilderTest {

  private static final double DURATION_IN_SECONDS = 0.5;
  private Velocity4dService velocity4dService;
  private StateEstimator stateEstimator;
  private VelocityController4d velocityController4d;
  private TimeProvider timeProvider;

  private static void checkCorrectServicesCalled(
      Velocity4dService velocity4dService,
      VelocityController4d velocityController4d,
      StateEstimator stateEstimator,
      TimeProvider timeProvider) {
    verify(velocity4dService, atLeastOnce())
        .sendInertialFrameVelocity(any(InertialFrameVelocity.class), any(Pose.class));
    verify(velocityController4d, atLeastOnce())
        .computeNextResponse(any(Pose.class), any(InertialFrameVelocity.class), anyDouble());
    verify(stateEstimator, atLeastOnce()).getCurrentState();
    verify(timeProvider, atLeastOnce()).getCurrentTime();
  }

  abstract void createAndExecuteCommand(ArgumentHolder argumentHolder);

  @Before
  public void setUp() {
    velocity4dService = mock(Velocity4dService.class, RETURNS_MOCKS);
    stateEstimator = mock(StateEstimator.class, RETURNS_MOCKS);
    when(stateEstimator.getCurrentState())
        .thenReturn(Optional.of(mock(DroneStateStamped.class, RETURNS_MOCKS)));

    velocityController4d = mock(VelocityController4d.class, RETURNS_MOCKS);
    timeProvider = mock(TimeProvider.class, RETURNS_MOCKS);
  }

  @Test
  public void testCorrectArgumentsCalled() {
    final ArgumentHolder argumentHolder =
        ArgumentHolder.builder()
            .velocityService(velocity4dService)
            .stateEstimator(stateEstimator)
            .velocityController4d(velocityController4d)
            .durationInSeconds(DURATION_IN_SECONDS)
            .timeProvider(timeProvider)
            .build();

    createAndExecuteCommand(argumentHolder);

    checkCorrectServicesCalled(
        velocity4dService, velocityController4d, stateEstimator, timeProvider);
  }

  @AutoValue
  abstract static class ArgumentHolder {
    public static Builder builder() {
      return new AutoValue_AbstractBuilderTest_ArgumentHolder.Builder();
    }

    abstract Velocity4dService velocityService();

    abstract StateEstimator stateEstimator();

    abstract VelocityController4d velocityController4d();

    abstract double durationInSeconds();

    abstract TimeProvider timeProvider();

    @AutoValue.Builder
    public abstract static class Builder {
      public abstract Builder velocityService(Velocity4dService value);

      public abstract Builder stateEstimator(StateEstimator value);

      public abstract Builder velocityController4d(VelocityController4d value);

      public abstract Builder durationInSeconds(double value);

      public abstract Builder timeProvider(TimeProvider value);

      public abstract ArgumentHolder build();
    }
  }
}
