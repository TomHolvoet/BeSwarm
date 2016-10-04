package commands.cratescommands;

import commands.AbstractFollowTrajectory;
import control.LinearPidController1d;
import control.Trajectory1d;
import control.dto.DroneStateStamped;
import localization.StateEstimator;
import org.ros.time.TimeProvider;
import services.Velocity3dService;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Follow trajectory command for Crates's drones. This command uses 3d velocity to control the
 * drones. That is, the command will sends velocity in x, y, and z in the inertial frame and the
 * desired yaw angle (NOT the yaw velocity) to the drone in each control loop.
 *
 * @author Hoang Tung Dinh
 */
public final class CratesFollowTrajectoryVel3d extends AbstractFollowTrajectory {

  private final Velocity3dService velocity3dService;
  private final LinearPidController1d pidControllerLinearX;
  private final LinearPidController1d pidControllerLinearY;
  private final LinearPidController1d pidControllerLinearZ;
  private final Trajectory1d trajectoryAngularZ;

  private CratesFollowTrajectoryVel3d(
      StateEstimator stateEstimator,
      double durationInSeconds,
      double controlRateInSeconds,
      double droneStateLifeDurationInSeconds,
      TimeProvider timeProvider,
      Velocity3dService velocity3dService,
      LinearPidController1d pidControllerLinearX,
      LinearPidController1d pidControllerLinearY,
      LinearPidController1d pidControllerLinearZ,
      Trajectory1d trajectoryAngularZ) {
    super(
        stateEstimator,
        durationInSeconds,
        controlRateInSeconds,
        droneStateLifeDurationInSeconds,
        timeProvider);
    this.velocity3dService = velocity3dService;
    this.pidControllerLinearX = pidControllerLinearX;
    this.pidControllerLinearY = pidControllerLinearY;
    this.pidControllerLinearZ = pidControllerLinearZ;
    this.trajectoryAngularZ = trajectoryAngularZ;
  }

  /**
   * Creates a builder for {@link CratesFollowTrajectoryVel3d}.
   *
   * @return a builder for {@link CratesFollowTrajectoryVel3d}
   */
  public static Builder builder() {
    return new Builder();
  }

  @Override
  protected AbstractControlLoop createControlLoop() {
    return new ControlLoop();
  }

  private final class ControlLoop extends AbstractControlLoop {

    private ControlLoop() {}

    @Override
    protected void computeAndSendResponse(
        double currentTimeInSeconds, DroneStateStamped currentState) {
      final double nextVelocityX =
          pidControllerLinearX.computeNextResponse(
              currentState.pose().x(),
              currentState.inertialFrameVelocity().linearX(),
              currentTimeInSeconds);
      final double nextVelocityY =
          pidControllerLinearY.computeNextResponse(
              currentState.pose().y(),
              currentState.inertialFrameVelocity().linearY(),
              currentTimeInSeconds);
      final double nextVelocityZ =
          pidControllerLinearZ.computeNextResponse(
              currentState.pose().z(),
              currentState.inertialFrameVelocity().linearZ(),
              currentTimeInSeconds);

      velocity3dService.sendVelocity3dMessage(
          nextVelocityX,
          nextVelocityY,
          nextVelocityZ,
          trajectoryAngularZ.getDesiredPosition(currentTimeInSeconds));
    }
  }

  /** {@code CratesFollowTrajectoryVel3d} builder static inner class. */
  public static final class Builder extends AbstractBuilder<Builder> {
    private Velocity3dService velocity3dService;
    private LinearPidController1d pidControllerLinearX;
    private LinearPidController1d pidControllerLinearY;
    private LinearPidController1d pidControllerLinearZ;
    private Trajectory1d trajectoryAngularZ;

    private Builder() {}

    @Override
    protected Builder self() {
      return this;
    }

    /**
     * Sets the {@code velocity3dService} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code velocity3dService} to set
     * @return a reference to this Builder
     */
    public Builder withVelocity3dService(Velocity3dService val) {
      velocity3dService = val;
      return this;
    }

    /**
     * Sets the {@code pidControllerLinearX} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code pidControllerLinearX} to set
     * @return a reference to this Builder
     */
    public Builder withPidControllerLinearX(LinearPidController1d val) {
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
    public Builder withPidControllerLinearY(LinearPidController1d val) {
      pidControllerLinearY = val;
      return this;
    }

    /**
     * Sets the {@code pidControllerLinearZ} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code pidControllerLinearZ} to set
     * @return a reference to this Builder
     */
    public Builder withPidControllerLinearZ(LinearPidController1d val) {
      pidControllerLinearZ = val;
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
     * Returns a {@code CratesFollowTrajectoryVel3d} built from the parameters previously set.
     *
     * @return a {@code CratesFollowTrajectoryVel3d} built with parameters of this {@code
     *     CratesFollowTrajectoryVel3d.Builder}
     */
    public CratesFollowTrajectoryVel3d build() {
      checkNotNull(durationInSeconds);
      checkNotNull(controlRateInSeconds);
      checkNotNull(droneStateLifeDurationInSeconds);
      checkNotNull(stateEstimator);
      checkNotNull(velocity3dService);
      checkNotNull(timeProvider);
      checkNotNull(pidControllerLinearX);
      checkNotNull(pidControllerLinearY);
      checkNotNull(pidControllerLinearZ);
      checkNotNull(trajectoryAngularZ);

      return new CratesFollowTrajectoryVel3d(
          stateEstimator,
          durationInSeconds,
          controlRateInSeconds,
          droneStateLifeDurationInSeconds,
          timeProvider,
          velocity3dService,
          pidControllerLinearX,
          pidControllerLinearY,
          pidControllerLinearZ,
          trajectoryAngularZ);
    }
  }
}
