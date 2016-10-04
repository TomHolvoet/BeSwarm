package commands.bebopcommands;

import commands.AbstractParrotFollowTrajectory;
import control.VelocityController4d;
import localization.StateEstimator;
import services.Velocity4dService;
import time.TimeProvider;

/**
 * Follow trajectory command for bebop.
 *
 * @author Hoang Tung Dinh
 */
public final class BebopFollowTrajectory extends AbstractParrotFollowTrajectory {

  private BebopFollowTrajectory(
      StateEstimator stateEstimator,
      double durationInSeconds,
      double controlRateInSeconds,
      double droneStateLifeDurationInSeconds,
      TimeProvider timeProvider,
      VelocityController4d velocityController4d,
      Velocity4dService velocity4dService) {
    super(
        stateEstimator,
        durationInSeconds,
        controlRateInSeconds,
        droneStateLifeDurationInSeconds,
        timeProvider,
        velocityController4d,
        velocity4dService);
  }

  /**
   * Creates a builder for {@link BebopFollowTrajectory}.
   *
   * @return a builder for {@link BebopFollowTrajectory}
   */
  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends AbstractParrotFollowTrajectory.ParrotBuilder<Builder> {

    private Builder() {}

    @Override
    protected Builder self() {
      return this;
    }

    /**
     * Returns a {@code BebopFollowTrajectory} built from the parameters previously set.
     *
     * @return a {@code BebopFollowTrajectory} built with parameters of this {@code
     *     BebopFollowTrajectory.Builder}
     */
    public BebopFollowTrajectory build() {
      checkMissingParameters();
      return new BebopFollowTrajectory(
          stateEstimator,
          durationInSeconds,
          controlRateInSeconds,
          droneStateLifeDurationInSeconds,
          timeProvider,
          velocityController4d,
          velocity4dService);
    }
  }
}
