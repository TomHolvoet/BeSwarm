package commands.tumsimcommands;

import commands.AbstractParrotFollowTrajectory;
import control.VelocityController4d;
import localization.StateEstimator;
import services.Velocity4dService;
import time.TimeProvider;

/**
 * Follow trajectory command for drones in Tum simulator.
 *
 * @author Hoang Tung Dinh
 */
public final class TumSimFollowTrajectory extends AbstractParrotFollowTrajectory {

  private TumSimFollowTrajectory(
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
   * Creates a builder for {@link TumSimFollowTrajectory}.
   *
   * @return a builder for {@link TumSimFollowTrajectory}
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
     * Returns a {@code TumSimFollowTrajectory} built from the parameters previously set.
     *
     * @return a {@code TumSimFollowTrajectory} built with parameters of this {@code
     *     TumSimFollowTrajectory.Builder}
     */
    public TumSimFollowTrajectory build() {
      checkMissingParameters();
      return new TumSimFollowTrajectory(
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
