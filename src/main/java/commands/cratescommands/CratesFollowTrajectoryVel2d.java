package commands.cratescommands;

import commands.AbstractFollowTrajectory;
import control.PidController1d;
import control.Trajectory1d;
import control.dto.DroneStateStamped;
import control.localization.StateEstimator;
import services.Velocity2dService;
import time.TimeProvider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Follow trajectory command for Crates's drones. This command uses 2d velocity to control the
 * drones. That is, the command will send the velocity in x and y in the inertial frame and the
 * desired z position and yaw position.
 *
 * @author Hoang Tung Dinh
 */
public final class CratesFollowTrajectoryVel2d extends AbstractFollowTrajectory {

  private final Velocity2dService velocity2dService;
  private final PidController1d pidControllerLinearX;
  private final PidController1d pidControllerLinearY;
  private final Trajectory1d trajectoryLinearZ;
  private final Trajectory1d trajectoryAngularZ;

  private CratesFollowTrajectoryVel2d(
      StateEstimator stateEstimator,
      double durationInSeconds,
      double controlRateInSeconds,
      double droneStateLifeDurationInSeconds,
      TimeProvider timeProvider,
      Velocity2dService velocity2dService,
      PidController1d pidControllerLinearX,
      PidController1d pidControllerLinearY,
      Trajectory1d trajectoryLinearZ,
      Trajectory1d trajectoryAngularZ) {
    super(
        stateEstimator,
        durationInSeconds,
        controlRateInSeconds,
        droneStateLifeDurationInSeconds,
        timeProvider);
    this.velocity2dService = velocity2dService;
    this.pidControllerLinearX = pidControllerLinearX;
    this.pidControllerLinearY = pidControllerLinearY;
    this.trajectoryLinearZ = trajectoryLinearZ;
    this.trajectoryAngularZ = trajectoryAngularZ;
  }

  /**
   * Creates a builder for {@link CratesFollowTrajectoryVel2d}.
   *
   * @return a builder for {@link CratesFollowTrajectoryVel2d}
   */
  public static Builder builder() {
    return new Builder();
  }

  @Override
  protected AbstractControlLoop createControlLoop() {
    return new ControlLoop();
  }

  private final class ControlLoop extends AbstractControlLoop {

    @Override
    protected void computeAndSendResponse(
        double currentTimeInSeconds, DroneStateStamped currentState) {
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
          trajectoryLinearZ.getDesiredPosition(currentTimeInSeconds),
          trajectoryAngularZ.getDesiredPosition(currentTimeInSeconds));
    }
  }

  /** {@code CratesFollowTrajectoryVel2d} builder static inner class. */
  public static final class Builder extends AbstractBuilder<Builder> {
    private Velocity2dService velocity2dService;
    private PidController1d pidControllerLinearX;
    private PidController1d pidControllerLinearY;
    private Trajectory1d trajectoryLinearZ;
    private Trajectory1d trajectoryAngularZ;

    private Builder() {}

    @Override
    protected Builder self() {
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
     * Sets the {@code pidControllerLinearX} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code pidControllerLinearX} to set
     * @return a reference to this Builder
     */
    public Builder withPidControllerLinearX(PidController1d val) {
      pidControllerLinearX = val;
      return this;
    }

    /**
     * Sets the {@code pidControllerLinearY} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code pidControllerLinearY} to set
     * @return a reference to this Builder
     */
    public Builder withPidControllerLinearY(PidController1d val) {
      pidControllerLinearY = val;
      return this;
    }

    /**
     * Sets the {@code trajectoryLinearZ} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code trajectoryLinearZ} to set
     * @return a reference to this Builder
     */
    public Builder withTrajectoryLinearZ(Trajectory1d val) {
      trajectoryLinearZ = val;
      return this;
    }

    /**
     * Sets the {@code trajectoryAngularZ} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code trajectoryAngularZ} to set
     * @return a reference to this Builder
     */
    public Builder withTrajectoryAngularZ(Trajectory1d val) {
      trajectoryAngularZ = val;
      return this;
    }

    /**
     * Returns a {@code CratesFollowTrajectoryVel2d} built from the parameters previously set.
     *
     * @return a {@code CratesFollowTrajectoryVel2d} built with parameters of this {@code
     *     CratesFollowTrajectoryVel2d.Builder}
     */
    public CratesFollowTrajectoryVel2d build() {
      checkNotNull(durationInSeconds);
      checkNotNull(pidControllerLinearX);
      checkNotNull(pidControllerLinearY);
      checkNotNull(controlRateInSeconds);
      checkNotNull(droneStateLifeDurationInSeconds);
      checkNotNull(stateEstimator);
      checkNotNull(velocity2dService);
      checkNotNull(timeProvider);
      checkNotNull(trajectoryLinearZ);
      checkNotNull(trajectoryAngularZ);

      return new CratesFollowTrajectoryVel2d(
          stateEstimator,
          durationInSeconds,
          controlRateInSeconds,
          droneStateLifeDurationInSeconds,
          timeProvider,
          velocity2dService,
          pidControllerLinearX,
          pidControllerLinearY,
          trajectoryLinearZ,
          trajectoryAngularZ);
    }
  }
}
