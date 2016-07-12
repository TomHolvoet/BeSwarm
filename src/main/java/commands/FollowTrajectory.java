package commands;

import com.google.common.base.Optional;
import commands.schedulers.PeriodicTaskRunner;
import control.PidController4d;
import control.PidParameters;
import control.Trajectory4d;
import control.dto.DroneStateStamped;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import control.localization.StateEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.VelocityService;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Hoang Tung Dinh
 */
public final class FollowTrajectory implements Command {

    private static final Logger logger = LoggerFactory.getLogger(FollowTrajectory.class);
    private static final Logger poseLogger = LoggerFactory.getLogger(FollowTrajectory.class.getName() + ".poselogger");
    private static final Logger velocityLogger = LoggerFactory.getLogger(
            FollowTrajectory.class.getName() + ".velocitylogger");

    private final VelocityService velocityService;
    private final StateEstimator stateEstimator;
    private final PidParameters pidLinearXParameters;
    private final PidParameters pidLinearYParameters;
    private final PidParameters pidLinearZParameters;
    private final PidParameters pidAngularZParameters;
    private final Trajectory4d trajectory4d;
    private final double durationInSeconds;
    private final double controlRateInSeconds;
    private final double droneStateLifeDurationInSeconds;

    private final VelocityController velocityController;

    private FollowTrajectory(Builder builder) {
        velocityService = builder.velocityService;
        stateEstimator = builder.stateEstimator;
        pidLinearXParameters = builder.pidLinearXParameters;
        pidLinearYParameters = builder.pidLinearYParameters;
        pidLinearZParameters = builder.pidLinearZParameters;
        pidAngularZParameters = builder.pidAngularZParameters;
        trajectory4d = builder.trajectory4d;
        durationInSeconds = builder.durationInSeconds;
        controlRateInSeconds = builder.controlRateInSeconds;
        droneStateLifeDurationInSeconds = builder.droneStateLifeDurationInSeconds;

        final CreateVelocityControllerVisitor controllerVisitor = CreateVelocityControllerVisitor.builder()
                .withTrajectory4d(trajectory4d)
                .withPidLinearXParameters(pidLinearXParameters)
                .withPidLinearYParameters(pidLinearYParameters)
                .withPidLinearZParameters(pidLinearZParameters)
                .withPidAngularZParameters(pidAngularZParameters)
                .build();

        velocityController = velocityService.accept(controllerVisitor);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder copyBuilder(AbstractFollowTrajectoryBuilder<?> otherBuilder) {
        return new Builder().copyOf(otherBuilder);
    }

    @Override
    public void execute() {
        logger.debug("Execute follow trajectory command: {}", trajectory4d);
        final Runnable computeNextResponse = new ComputeNextResponse();
        PeriodicTaskRunner.run(computeNextResponse, controlRateInSeconds, durationInSeconds);
    }

    private final class ComputeNextResponse implements Runnable {
        private final PidController4d pidController4d;
        private final double startTimeInNanoSeconds;
        private final int stateLifeDurationInNumberOfControlLoops;

        private int counter = 0;
        private double lastTimeStamp = Double.MIN_VALUE;
        private final InertialFrameVelocity zeroVelocity = Velocity.createZeroVelocity();
        private final Pose zeroPose = Pose.createZeroPose();

        private static final double NANO_SECOND_TO_SECOND = 1.0E09;

        private ComputeNextResponse() {
            this.pidController4d = PidController4d.builder()
                    .linearXParameters(pidLinearXParameters)
                    .linearYParameters(pidLinearYParameters)
                    .linearZParameters(pidLinearZParameters)
                    .angularZParameters(pidAngularZParameters)
                    .trajectory4d(trajectory4d)
                    .build();

            this.startTimeInNanoSeconds = System.nanoTime();
            this.stateLifeDurationInNumberOfControlLoops = (int) Math.ceil(
                    droneStateLifeDurationInSeconds / controlRateInSeconds);
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
                final double currentTimeInSeconds = (System.nanoTime() - startTimeInNanoSeconds) /
                        NANO_SECOND_TO_SECOND;
                velocityController.computeAndSendVelocity(currentTimeInSeconds, currentState.get());
                logDroneState(currentState.get(), currentTimeInSeconds);
            }
        }

        private void logDroneState(DroneStateStamped currentState, double currentTimeInSeconds) {
            poseLogger.trace("{} {} {} {} {} {} {} {} {}", System.nanoTime() / NANO_SECOND_TO_SECOND,
                    currentState.pose().x(), currentState.pose().y(), currentState.pose().z(),
                    currentState.pose().yaw(), trajectory4d.getDesiredPositionX(currentTimeInSeconds),
                    trajectory4d.getDesiredPositionY(currentTimeInSeconds),
                    trajectory4d.getDesiredPositionZ(currentTimeInSeconds),
                    trajectory4d.getDesiredAngleZ(currentTimeInSeconds));
            velocityLogger.trace("{} {} {} {} {} {} {} {} {}", System.nanoTime() / NANO_SECOND_TO_SECOND,
                    currentState.inertialFrameVelocity().linearX(), currentState.inertialFrameVelocity().linearY(),
                    currentState.inertialFrameVelocity().linearZ(), currentState.inertialFrameVelocity().angularZ(),
                    trajectory4d.getDesiredVelocityX(currentTimeInSeconds),
                    trajectory4d.getDesiredVelocityY(currentTimeInSeconds),
                    trajectory4d.getDesiredVelocityZ(currentTimeInSeconds),
                    trajectory4d.getDesiredAngularVelocityZ(currentTimeInSeconds));
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

    public static final class Builder extends AbstractFollowTrajectoryBuilder<Builder> {
        private Trajectory4d trajectory4d;
        private double durationInSeconds;

        private Builder() {
            super();
        }

        @Override
        Builder self() {
            return this;
        }

        public Builder withTrajectory4d(Trajectory4d val) {
            trajectory4d = val;
            return this;
        }

        public Builder withDurationInSeconds(double val) {
            durationInSeconds = val;
            return this;
        }

        public FollowTrajectory build() {
            checkNotNull(trajectory4d);
            checkNotNull(durationInSeconds);
            checkNotNull(super.pidLinearXParameters);
            checkNotNull(super.pidLinearYParameters);
            checkNotNull(super.pidLinearZParameters);
            checkNotNull(super.pidAngularZParameters);
            checkNotNull(super.controlRateInSeconds);
            checkNotNull(super.droneStateLifeDurationInSeconds);
            checkNotNull(super.stateEstimator);
            checkNotNull(super.velocityService);

            return new FollowTrajectory(this);
        }
    }
}
