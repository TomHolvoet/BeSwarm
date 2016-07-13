package commands;

import com.google.common.base.Optional;
import commands.schedulers.PeriodicTaskRunner;
import control.Trajectory4d;
import control.dto.DroneStateStamped;
import control.localization.StateEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Hoang Tung Dinh
 */
public final class FollowTrajectory implements Command {

    private static final Logger logger = LoggerFactory.getLogger(FollowTrajectory.class);
    private static final Logger poseLogger = LoggerFactory.getLogger(FollowTrajectory.class.getName() + ".poselogger");
    private static final Logger velocityLogger = LoggerFactory.getLogger(
            FollowTrajectory.class.getName() + ".velocitylogger");

    private final StateEstimator stateEstimator;
    private final Trajectory4d trajectory4d;
    private final double durationInSeconds;
    private final double controlRateInSeconds;
    private final double droneStateLifeDurationInSeconds;

    private final VelocityController velocityController;

    private FollowTrajectory(Builder builder) {
        stateEstimator = builder.stateEstimator;
        trajectory4d = builder.trajectory4d;
        durationInSeconds = builder.durationInSeconds;
        controlRateInSeconds = builder.controlRateInSeconds;
        droneStateLifeDurationInSeconds = builder.droneStateLifeDurationInSeconds;

        final CreateVelocityControllerVisitor controllerVisitor = CreateVelocityControllerVisitor.builder()
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

    private final class ComputeNextResponse implements Runnable {
        private final double startTimeInNanoSeconds;
        private final int stateLifeDurationInNumberOfControlLoops;

        private int counter = 0;
        private double lastTimeStamp = Double.MIN_VALUE;

        private static final double NANO_SECOND_TO_SECOND = 1.0E09;

        private ComputeNextResponse() {
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

    /**
     * Builder for {@link FollowTrajectory}.
     */
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

        /**
         * Sets the trajectory that the drone will follow.
         *
         * @return a reference to this Builder
         */
        public Builder withTrajectory4d(Trajectory4d val) {
            trajectory4d = val;
            return this;
        }

        /**
         * Sets the duration that the {@link FollowTrajectory} will be executed.
         *
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
