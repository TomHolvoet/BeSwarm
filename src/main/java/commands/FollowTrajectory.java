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
import services.Velocity4dService;

/**
 * @author Hoang Tung Dinh
 */
public final class FollowTrajectory implements Command {

    private static final Logger logger = LoggerFactory.getLogger(FollowTrajectory.class);
    private static final Logger poseLogger = LoggerFactory.getLogger(FollowTrajectory.class.getName() + ".poselogger");
    private static final Logger velocityLogger = LoggerFactory.getLogger(
            FollowTrajectory.class.getName() + ".velocitylogger");

    private final Velocity4dService velocity4dService;
    private final StateEstimator stateEstimator;
    private final PidParameters pidLinearXParameters;
    private final PidParameters pidLinearYParameters;
    private final PidParameters pidLinearZParameters;
    private final PidParameters pidAngularZParameters;
    private final Trajectory4d trajectory4d;
    private final double durationInSeconds;
    private final double controlRateInSeconds;
    private final double droneStateLifeDurationInSeconds;

    private FollowTrajectory(Builder builder) {
        velocity4dService = builder.getVelocity4dService();
        stateEstimator = builder.getStateEstimator();
        pidLinearXParameters = builder.getPidLinearXParameters();
        pidLinearYParameters = builder.getPidLinearYParameters();
        pidLinearZParameters = builder.getPidLinearZParameters();
        pidAngularZParameters = builder.getPidAngularZParameters();
        trajectory4d = builder.getTrajectory4d();
        durationInSeconds = builder.getDurationInSeconds();
        controlRateInSeconds = builder.getControlRateInSeconds();
        droneStateLifeDurationInSeconds = builder.getDroneStateLifeDurationInSeconds();
    }

    public static CommandBuilders.VelocityServiceStep<Trajectory4dStep> builder() {
        return new Builder();
    }

    public static Trajectory4dStep copyBuilder(CommandBuilders.AbstractFollowTrajectoryBuilder<?, ?> otherBuilder) {
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
                logger.trace("Cannot get state. Send zero velocity.");
                velocity4dService.sendVelocity4dMessage(zeroVelocity, zeroPose);
                return;
            }

            setCounter(currentState.get());

            if (counter >= stateLifeDurationInNumberOfControlLoops) {
                logger.debug("Pose is outdated. Stop sending velocity.");
            } else {
                logger.trace("Got pose and velocity. Start computing the next velocity response.");
                final double currentTimeInSeconds = (System.nanoTime() - startTimeInNanoSeconds) /
                        NANO_SECOND_TO_SECOND;
                final InertialFrameVelocity nextVelocity = pidController4d.compute(currentState.get().pose(),
                        currentState.get().inertialFrameVelocity(), currentTimeInSeconds);
                velocity4dService.sendVelocity4dMessage(nextVelocity, currentState.get().pose());
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

    public interface Trajectory4dStep {
        DurationInSecondsStep withTrajectory4d(Trajectory4d val);
    }

    public interface DurationInSecondsStep {
        CommandBuilders.BuildStep<FollowTrajectory> withDurationInSeconds(double val);
    }

    public static final class Builder extends CommandBuilders.AbstractFollowTrajectoryBuilder<Trajectory4dStep,
            FollowTrajectory> implements Trajectory4dStep, DurationInSecondsStep {
        private Trajectory4d trajectory4d;
        private double durationInSeconds;

        private Builder() {
            super();
        }

        @Override
        Trajectory4dStep nextInterfaceInBuilderChain() {
            return this;
        }

        @Override
        public DurationInSecondsStep withTrajectory4d(Trajectory4d val) {
            trajectory4d = val;
            return this;
        }

        @Override
        public CommandBuilders.BuildStep<FollowTrajectory> withDurationInSeconds(double val) {
            durationInSeconds = val;
            return this;
        }

        @Override
        public FollowTrajectory build() {
            return new FollowTrajectory(this);
        }

        public Trajectory4d getTrajectory4d() {
            return trajectory4d;
        }

        public double getDurationInSeconds() {
            return durationInSeconds;
        }
    }
}
