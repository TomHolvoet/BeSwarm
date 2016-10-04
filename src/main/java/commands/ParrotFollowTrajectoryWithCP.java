package commands;

import com.google.common.base.Optional;
import commands.schedulers.PeriodicTaskRunner;
import control.FiniteTrajectory4d;
import control.PidParameters;
import control.dto.BodyFrameVelocity;
import control.dto.DroneStateStamped;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import ilog.concert.IloException;
import localization.StateEstimator;
import monitors.PoseOutdatedMonitor;
import org.ros.time.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Velocity4dService;
import solvers.RatsProblemAssembler;

import static com.google.common.base.Preconditions.checkNotNull;

/** @author Hoang Tung Dinh */
public final class ParrotFollowTrajectoryWithCP implements Command {

  private static final Logger logger = LoggerFactory.getLogger(ParrotFollowTrajectoryWithCP.class);

  private final StateEstimator stateEstimator;
  private final PoseOutdatedMonitor poseOutdatedMonitor;
  private final FiniteTrajectory4d trajectory;
  private final double controlRateInSeconds;
  private final TimeProvider timeProvider;
  private final PidParameters pidLinearX;
  private final PidParameters pidLinearY;
  private final PidParameters pidLinearZ;
  private final PidParameters pidAngularZ;
  private final Velocity4dService velocity4dService;

  private ParrotFollowTrajectoryWithCP(Builder builder) {
    stateEstimator = builder.stateEstimator;
    poseOutdatedMonitor = builder.poseOutdatedMonitor;
    trajectory = builder.trajectory;
    controlRateInSeconds = builder.controlRateInSeconds;
    timeProvider = builder.timeProvider;
    pidLinearX = builder.pidLinearX;
    pidLinearY = builder.pidLinearY;
    pidLinearZ = builder.pidLinearZ;
    pidAngularZ = builder.pidAngularZ;
    velocity4dService = builder.velocity4dService;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public void execute() {
    logger.debug("Start executing ParrotFollowTrajectoryWithCP command.");
    final Runnable controlLoop = new ControlLoop();
    PeriodicTaskRunner.run(controlLoop, controlRateInSeconds, trajectory.getTrajectoryDuration());
  }

  private final class ControlLoop implements Runnable {

    private final double startTimeInSecs;

    private ControlLoop() {
      this.startTimeInSecs = timeProvider.getCurrentTime().toSeconds();
    }

    @Override
    public void run() {
      final Optional<DroneStateStamped> currentState = stateEstimator.getCurrentState();
      // TODO: fix the lagTimeInSeconds confusion
      final double time =
          timeProvider.getCurrentTime().toSeconds()
              - startTimeInSecs
              + pidLinearX.lagTimeInSeconds();
      final Pose desiredPose = Pose.createFromTrajectory(trajectory, time);
      final InertialFrameVelocity desiredVelocity = Velocity.createFromTrajectory(trajectory, time);

      if (currentState.isPresent()) {
        try {
          final RatsProblemAssembler ratsProblem =
              RatsProblemAssembler.builder()
                  .withCurrentPose(currentState.get().pose())
                  .withDesiredPose(desiredPose)
                  .withCurrentRefVelocity(currentState.get().inertialFrameVelocity())
                  .withDesiredRefVelocity(desiredVelocity)
                  .withPoseValid(
                      poseOutdatedMonitor.getPoseStatus() == PoseOutdatedMonitor.PoseStatus.VALID
                          ? 1
                          : 0)
                  .withPidLinearX(pidLinearX)
                  .withPidLinearY(pidLinearY)
                  .withPidLinearZ(pidLinearZ)
                  .withPidAngularZ(pidAngularZ)
                  .build();

          final Optional<BodyFrameVelocity> bodyFrameVelocity = ratsProblem.solve();

          if (bodyFrameVelocity.isPresent()) {
            velocity4dService.sendBodyFrameVelocity(bodyFrameVelocity.get());
          }

        } catch (IloException e) {
          logger.info("Exception in constraint solver.", e);
        }
      }
    }
  }

  /** {@code ParrotFollowTrajectoryWithCP} builder static inner class. */
  public static final class Builder {
    private StateEstimator stateEstimator;
    private PoseOutdatedMonitor poseOutdatedMonitor;
    private FiniteTrajectory4d trajectory;
    private Double controlRateInSeconds;
    private TimeProvider timeProvider;
    private PidParameters pidLinearX;
    private PidParameters pidLinearY;
    private PidParameters pidLinearZ;
    private PidParameters pidAngularZ;
    private Velocity4dService velocity4dService;

    private Builder() {}

    /**
     * Sets the {@code stateEstimator} and returns a reference to this Builder so that the methods
     * can be chained together.
     *
     * @param val the {@code stateEstimator} to set
     * @return a reference to this Builder
     */
    public Builder withStateEstimator(StateEstimator val) {
      stateEstimator = val;
      return this;
    }

    /**
     * Sets the {@code poseOutdatedMonitor} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code poseOutdatedMonitor} to set
     * @return a reference to this Builder
     */
    public Builder withPoseOutdatedMonitor(PoseOutdatedMonitor val) {
      poseOutdatedMonitor = val;
      return this;
    }

    /**
     * Sets the {@code trajectory} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code trajectory} to set
     * @return a reference to this Builder
     */
    public Builder withTrajectory(FiniteTrajectory4d val) {
      trajectory = val;
      return this;
    }

    /**
     * Sets the {@code controlRateInSeconds} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code controlRateInSeconds} to set
     * @return a reference to this Builder
     */
    public Builder withControlRateInSeconds(double val) {
      controlRateInSeconds = val;
      return this;
    }

    /**
     * Sets the {@code timeProvider} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code timeProvider} to set
     * @return a reference to this Builder
     */
    public Builder withTimeProvider(TimeProvider val) {
      timeProvider = val;
      return this;
    }

    /**
     * Sets the {@code pidLinearX} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code pidLinearX} to set
     * @return a reference to this Builder
     */
    public Builder withPidLinearX(PidParameters val) {
      pidLinearX = val;
      return this;
    }

    /**
     * Sets the {@code pidLinearY} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code pidLinearY} to set
     * @return a reference to this Builder
     */
    public Builder withPidLinearY(PidParameters val) {
      pidLinearY = val;
      return this;
    }

    /**
     * Sets the {@code pidLinearZ} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code pidLinearZ} to set
     * @return a reference to this Builder
     */
    public Builder withPidLinearZ(PidParameters val) {
      pidLinearZ = val;
      return this;
    }

    /**
     * Sets the {@code pidAngularZ} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code pidAngularZ} to set
     * @return a reference to this Builder
     */
    public Builder withPidAngularZ(PidParameters val) {
      pidAngularZ = val;
      return this;
    }

    /**
     * Sets the {@code velocity4dService} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code velocity4dService} to set
     * @return a reference to this Builder
     */
    public Builder withVelocity4dService(Velocity4dService val) {
      velocity4dService = val;
      return this;
    }

    /**
     * Returns a {@code ParrotFollowTrajectoryWithCP} built from the parameters previously set.
     *
     * @return a {@code ParrotFollowTrajectoryWithCP} built with parameters of this {@code
     *     ParrotFollowTrajectoryWithCP.Builder}
     */
    public ParrotFollowTrajectoryWithCP build() {
      checkNotNull(stateEstimator);
      checkNotNull(poseOutdatedMonitor);
      checkNotNull(trajectory);
      checkNotNull(controlRateInSeconds);
      checkNotNull(timeProvider);
      checkNotNull(pidLinearX);
      checkNotNull(pidLinearY);
      checkNotNull(pidLinearZ);
      checkNotNull(pidAngularZ);
      checkNotNull(velocity4dService);
      return new ParrotFollowTrajectoryWithCP(this);
    }
  }
}
