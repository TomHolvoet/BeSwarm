package commands;

import com.google.common.base.Optional;
import commands.schedulers.PeriodicTaskRunner;
import control.Trajectory4d;
import control.dto.DroneStateStamped;
import control.localization.StateEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import time.TimeProvider;

import static com.google.common.base.Preconditions.checkNotNull;

/** @author Hoang Tung Dinh */
public final class FollowTrajectory implements Command {

  private static final Logger logger = LoggerFactory.getLogger(FollowTrajectory.class);
  private static final Logger poseLogger =
      LoggerFactory.getLogger(FollowTrajectory.class.getName() + ".poselogger");
  private static final Logger velocityLogger =
      LoggerFactory.getLogger(FollowTrajectory.class.getName() + ".velocitylogger");

  private final StateEstimator stateEstimator;
  private final Trajectory4d trajectory4d;
  private final double durationInSeconds;
  private final double controlRateInSeconds;
  private final double droneStateLifeDurationInSeconds;
  private final TimeProvider timeProvider;

  private final VelocityController velocityController;

  private FollowTrajectory(Builder builder) {
    stateEstimator = builder.stateEstimator;
    trajectory4d = builder.trajectory4d;
    durationInSeconds = builder.durationInSeconds;
    controlRateInSeconds = builder.controlRateInSeconds;
    droneStateLifeDurationInSeconds = builder.droneStateLifeDurationInSeconds;
    timeProvider = builder.timeProvider;

    final CreateVelocityControllerVisitor controllerVisitor =
        CreateVelocityControllerVisitor.builder()
            .withTrajectory4d(trajectory4d)
            .withPidLinearXParameters(builder.pidLinearXParameters)
            .withPidLinearYParameters(builder.pidLinearYParameters)
            .withPidLinearZParameters(builder.pidLinearZParameters)
            .withPidAngularZParameters(builder.pidAngularZParameters)
            .build();

    velocityController = controllerVisitor.createVelocityController(builder.velocityService);
  }

  /**
   * Gets the builder of this class.
   *
   * @return a builder instance
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Copies the parameters of another builder.
   *
   * @param otherBuilder the other builder
   * @return a builder instance of this class with all copied parameters from the other builder
   */
  public static Builder copyBuilder(AbstractFollowTrajectoryBuilder<?> otherBuilder) {
    return new Builder().copyOf(otherBuilder);
  }

  @Override
  public void execute() {
    logger.debug("Execute follow trajectory command: {}", trajectory4d);
    final Runnable computeNextResponse = new ComputeNextResponse();
    PeriodicTaskRunner.run(computeNextResponse, controlRateInSeconds, durationInSeconds);
  }

  /** Builder for {@link FollowTrajectory}. */
  public static final class Builder extends AbstractFollowTrajectoryBuilder<Builder> {
    private Trajectory4d trajectory4d;
    private Double durationInSeconds;

    private Builder() {}

    @Override
    Builder self() {
      return this;
    }

    /**
     * Sets the trajectory that the drone will follow.
     *
     * @param val the value to set
     * @return a reference to this Builder
     */
    public Builder withTrajectory4d(Trajectory4d val) {
      trajectory4d = val;
      return this;
    }

    /**
     * Sets the duration that the {@link FollowTrajectory} will be executed.
     *
     * @param val the value to set
     * @return a reference to this Builder
     */
    public Builder withDurationInSeconds(double val) {
      durationInSeconds = val;
      return this;
    }

    /**
     * Builds a {@link FollowTrajectory} instance.
     *
     * @return a built {@link FollowTrajectory} instance
     */
    public FollowTrajectory build() {
      checkNotNull(trajectory4d);
      checkNotNull(durationInSeconds);
      checkNotNull(pidLinearXParameters);
      checkNotNull(pidLinearYParameters);
      checkNotNull(pidLinearZParameters);
      checkNotNull(pidAngularZParameters);
      checkNotNull(controlRateInSeconds);
      checkNotNull(droneStateLifeDurationInSeconds);
      checkNotNull(stateEstimator);
      checkNotNull(velocityService);
      checkNotNull(timeProvider);

      return new FollowTrajectory(this);
    }
  }

  private final class ComputeNextResponse implements Runnable {
    private final double startTimeInSeconds;
    private final int stateLifeDurationInNumberOfControlLoops;
    // assigned to 0
    private int counter;
    private double lastTimeStamp = Double.MIN_VALUE;

    private ComputeNextResponse() {
      this.startTimeInSeconds = timeProvider.getCurrentTimeSeconds();
      this.stateLifeDurationInNumberOfControlLoops =
          (int) Math.ceil(droneStateLifeDurationInSeconds / controlRateInSeconds);
    }

    @Override
    public void run() {
      logger.trace("Start a control loop.");
      final Optional<DroneStateStamped> currentState = stateEstimator.getCurrentState();
      if (!currentState.isPresent()) {
        logger.trace("Cannot get state. Haven't sent any velocity.");
        return;
      }

      setCounter(currentState.get());

      if (counter >= stateLifeDurationInNumberOfControlLoops) {
        logger.debug("Pose is outdated. Stop sending velocity.");
      } else {
        logger.trace("Got pose and velocity. Start computing the next velocity response.");
        final double currentTimeInSeconds =
            timeProvider.getCurrentTimeSeconds() - startTimeInSeconds;
        velocityController.computeAndSendVelocity(currentTimeInSeconds, currentState.get());
        logDroneState(currentState.get(), currentTimeInSeconds);
      }
    }

    private void logDroneState(DroneStateStamped currentState, double currentTimeInSeconds) {
      final double systemTimeInSeconds = timeProvider.getCurrentTimeSeconds();
      poseLogger.trace(
          "{} {} {} {} {} {} {} {} {}",
          systemTimeInSeconds,
          currentState.pose().x(),
          currentState.pose().y(),
          currentState.pose().z(),
          currentState.pose().yaw(),
          trajectory4d.getDesiredPositionX(currentTimeInSeconds),
          trajectory4d.getDesiredPositionY(currentTimeInSeconds),
          trajectory4d.getDesiredPositionZ(currentTimeInSeconds),
          trajectory4d.getDesiredAngleZ(currentTimeInSeconds));

      final double deltaTimeInSeconds = 0.1;
      final double desiredVelocityX =
          (trajectory4d.getDesiredPositionX(currentTimeInSeconds + deltaTimeInSeconds)
                  - trajectory4d.getDesiredPositionX(currentTimeInSeconds))
              / deltaTimeInSeconds;
      final double desiredVelocityY =
          (trajectory4d.getDesiredPositionY(currentTimeInSeconds + deltaTimeInSeconds)
                  - trajectory4d.getDesiredPositionY(currentTimeInSeconds))
              / deltaTimeInSeconds;
      final double desiredVelocityZ =
          (trajectory4d.getDesiredPositionZ(currentTimeInSeconds + deltaTimeInSeconds)
                  - trajectory4d.getDesiredPositionZ(currentTimeInSeconds))
              / deltaTimeInSeconds;
      final double desiredVelocityYaw =
          (trajectory4d.getDesiredAngleZ(currentTimeInSeconds + deltaTimeInSeconds)
                  - trajectory4d.getDesiredAngleZ(currentTimeInSeconds))
              / deltaTimeInSeconds;

      velocityLogger.trace(
          "{} {} {} {} {} {} {} {} {}",
          systemTimeInSeconds,
          currentState.inertialFrameVelocity().linearX(),
          currentState.inertialFrameVelocity().linearY(),
          currentState.inertialFrameVelocity().linearZ(),
          currentState.inertialFrameVelocity().angularZ(),
          desiredVelocityX,
          desiredVelocityY,
          desiredVelocityZ,
          desiredVelocityYaw);
    }

    private void setCounter(DroneStateStamped currentState) {
      final double currentTimeStamp = currentState.getTimeStampInSeconds();
      if (currentTimeStamp == lastTimeStamp) {
        counter++;
      } else {
        counter = 0;
        lastTimeStamp = currentTimeStamp;
      }
    }
  }
}
