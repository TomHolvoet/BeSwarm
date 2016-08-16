package commands;

import applications.trajectory.TrajectoryUtils;
import control.DefaultPidParameters;
import control.PidController1d;
import control.PidParameters;
import control.Trajectory4d;
import control.dto.DroneStateStamped;
import services.Velocity2dService;

import static com.google.common.base.Preconditions.checkNotNull;

/** @author Hoang Tung Dinh */
final class Velocity2dController implements VelocityController {

  private final Trajectory4d trajectory4d;
  private final Velocity2dService velocity2dService;
  private final PidController1d pidControllerLinearX;
  private final PidController1d pidControllerLinearY;

  private Velocity2dController(Builder builder) {
    trajectory4d = builder.trajectory4d;
    velocity2dService = builder.velocity2dService;
    pidControllerLinearX =
        PidController1d.create(
            builder.pidLinearX, TrajectoryUtils.getTrajectoryLinearX(trajectory4d));
    pidControllerLinearY =
        PidController1d.create(
            builder.pidLinearY, TrajectoryUtils.getTrajectoryLinearY(trajectory4d));
  }

  /**
   * Gets the builder of this class.
   *
   * @return a builder instance
   */
  public static Builder builder() {
    return new Builder();
  }

  @Override
  public void computeAndSendVelocity(double currentTimeInSeconds, DroneStateStamped currentState) {
    final double nextVelocityX =
        pidControllerLinearX.compute(
            currentState.pose().x(),
            currentState.inertialFrameVelocity().linearX(),
            currentTimeInSeconds);
    final double nextVelocityY =
        pidControllerLinearY.compute(
            currentState.pose().y(),
            currentState.inertialFrameVelocity().linearY(),
            currentTimeInSeconds);

    velocity2dService.sendVelocityHeightMessage(
        nextVelocityX,
        nextVelocityY,
        trajectory4d.getDesiredPositionZ(currentTimeInSeconds),
        trajectory4d.getDesiredAngleZ(currentTimeInSeconds));
  }

  /** Builds a {@link Velocity2dController} instance. */
  static final class Builder extends BuilderWithVelocity2dService<Builder> {

    private Velocity2dService velocity2dService;

    Builder() {}

    @Override
    Builder self() {
      return this;
    }

    /**
     * Sets the {@code velocity2dService} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code velocity2dService} to set
     * @return a reference to this Builder
     */
    public Builder withVelocity2dService(Velocity2dService val) {
      velocity2dService = val;
      return this;
    }

    /**
     * Returns a {@code Velocity2dController} built from the parameters previously set.
     *
     * @return a {@code Velocity2dController} built with parameters of this {@code
     *     Velocity2dController.Builder}
     */
    public Velocity2dController build() {
      checkNotNull(velocity2dService);
      checkNotNull(trajectory4d);
      checkNotNull(pidLinearX);
      checkNotNull(pidLinearY);
      return new Velocity2dController(this);
    }
  }

  /** {@code Velocity2dController} builder static inner class. */
  abstract static class BuilderWithVelocity2dService<T extends BuilderWithVelocity2dService<T>> {
    Trajectory4d trajectory4d;
    PidParameters pidLinearX;
    PidParameters pidLinearY;

    BuilderWithVelocity2dService() {
      pidLinearX = DefaultPidParameters.LINEAR_X.getParameters();
      pidLinearY = DefaultPidParameters.LINEAR_Y.getParameters();
    }

    abstract T self();

    /**
     * Sets the {@code trajectory4d} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code trajectory4d} to set
     * @return a reference to this Builder
     */
    public final T withTrajectory4d(Trajectory4d val) {
      trajectory4d = val;
      return self();
    }

    /**
     * Sets the {@code pidLinearX} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code pidLinearX} to set
     * @return a reference to this Builder
     */
    public final T withPidLinearX(PidParameters val) {
      pidLinearX = val;
      return self();
    }

    /**
     * Sets the {@code pidLinearY} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code pidLinearY} to set
     * @return a reference to this Builder
     */
    public final T withPidLinearY(PidParameters val) {
      pidLinearY = val;
      return self();
    }
  }
}
