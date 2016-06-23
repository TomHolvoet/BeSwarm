package commands;

import com.google.common.base.Optional;
import commands.schedulers.PeriodicTaskRunner;
import control.DefaultPidParameters;
import control.PidController4d;
import control.PidParameters;
import control.Trajectory4d;
import control.dto.Pose;
import control.dto.Velocity;
import control.localization.PoseEstimator;
import control.localization.VelocityEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.VelocityService;

/**
 * Command for moving to a predefined pose. It is a facade which uses {@link Move}.
 *
 * @author Hoang Tung Dinh
 */
public final class FollowTrajectory implements Command {

    private static final Logger logger = LoggerFactory.getLogger(FollowTrajectory.class);

    private final VelocityService velocityService;
    private final PoseEstimator poseEstimator;
    private final VelocityEstimator velocityEstimator;
    private final PidParameters pidLinearParameters;
    private final PidParameters pidAngularParameters;
    private final Trajectory4d trajectory4d;
    private final double durationInSeconds;
    private final double controlRateInSeconds;

    private static final double DEFAULT_CONTROL_RATE_IN_SECONDS = 0.05;

    private FollowTrajectory(Builder builder) {
        velocityService = builder.velocityService;
        poseEstimator = builder.poseEstimator;
        velocityEstimator = builder.velocityEstimator;
        pidLinearParameters = builder.pidLinearParameters;
        pidAngularParameters = builder.pidAngularParameters;
        trajectory4d = builder.trajectory4d;
        durationInSeconds = builder.durationInSeconds;
        controlRateInSeconds = builder.controlRateInSeconds;
    }

    /**
     * {@link Builder#controlRateInSeconds(double)}, {@link Builder#pidLinearParameters(PidParameters)} and
     * {@link Builder#pidLinearParameters(PidParameters)} are optional. All other parameters are mandatory.
     *
     * @return a builder
     */
    public static Builder builder() {
        return new Builder().controlRateInSeconds(DEFAULT_CONTROL_RATE_IN_SECONDS)
                .pidLinearParameters(DefaultPidParameters.DEFAULT_LINEAR_PARAMETERS.getParameters())
                .pidAngularParameters(DefaultPidParameters.DEFAULT_ANGULAR_PARAMETERS.getParameters());
    }

    @Override
    public void execute() {
        logger.debug("Execute follow trajectory command.");

        final PidController4d pidController4d = PidController4d.builder()
                .linearXParameters(pidLinearParameters)
                .linearYParameters(pidLinearParameters)
                .linearZParameters(pidLinearParameters)
                .angularZParameters(pidAngularParameters)
                .trajectory4d(trajectory4d)
                .build();

        final Runnable computeNextResponse = new ComputeNextResponse(pidController4d);
        PeriodicTaskRunner.run(computeNextResponse, controlRateInSeconds, durationInSeconds);
    }

    private final class ComputeNextResponse implements Runnable {
        private final PidController4d pidController4d;

        private ComputeNextResponse(PidController4d pidController4d) {
            this.pidController4d = pidController4d;
        }

        @Override
        public void run() {
            logger.debug("Start a control loop.");
            final Optional<Pose> currentPose = poseEstimator.getCurrentPose();
            if (!currentPose.isPresent()) {
                logger.debug("Cannot get pose.");
                return;
            }

            final Optional<Velocity> currentVelocityInGlobalFrame = velocityEstimator.getCurrentVelocity();
            if (!currentVelocityInGlobalFrame.isPresent()) {
                logger.debug("Cannot get velocity.");
                return;
            }

            logger.debug("Got pose and velocity. Start computing the next velocity response.");
            final Velocity nextVelocityInGlobalFrame = pidController4d.compute(currentPose.get(),
                    currentVelocityInGlobalFrame.get());
            final Velocity nextVelocityInLocalFrame = Velocity.createLocalVelocityFromGlobalVelocity(
                    nextVelocityInGlobalFrame, currentPose.get().yaw());
            velocityService.sendVelocityMessage(nextVelocityInLocalFrame);
        }
    }

    /**
     * {@code FollowTrajectory} builder static inner class.
     */
    public static final class Builder {
        private VelocityService velocityService;
        private PoseEstimator poseEstimator;
        private VelocityEstimator velocityEstimator;
        private PidParameters pidLinearParameters;
        private PidParameters pidAngularParameters;
        private Trajectory4d trajectory4d;
        private double durationInSeconds;
        private double controlRateInSeconds;

        private Builder() {}

        /**
         * Sets the {@code velocityService} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code velocityService} to set
         * @return a reference to this Builder
         */
        public Builder velocityPublisher(VelocityService val) {
            velocityService = val;
            return this;
        }

        /**
         * Sets the {@code poseEstimator} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code poseEstimator} to set
         * @return a reference to this Builder
         */
        public Builder poseEstimator(PoseEstimator val) {
            poseEstimator = val;
            return this;
        }

        /**
         * Sets the {@code velocityEstimator} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code velocityEstimator} to set
         * @return a reference to this Builder
         */
        public Builder velocityEstimator(VelocityEstimator val) {
            velocityEstimator = val;
            return this;
        }

        /**
         * Sets the {@code pidLinearParameters} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code pidLinearParameters} to set
         * @return a reference to this Builder
         */
        public Builder pidLinearParameters(PidParameters val) {
            pidLinearParameters = val;
            return this;
        }

        /**
         * Sets the {@code pidAngularParameters} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code pidAngularParameters} to set
         * @return a reference to this Builder
         */
        public Builder pidAngularParameters(PidParameters val) {
            pidAngularParameters = val;
            return this;
        }

        /**
         * Sets the {@code trajectory4d} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code trajectory4d} to set
         * @return a reference to this Builder
         */
        public Builder trajectory4d(Trajectory4d val) {
            trajectory4d = val;
            return this;
        }

        /**
         * Sets the {@code durationInSeconds} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code durationInSeconds} to set
         * @return a reference to this Builder
         */
        public Builder durationInSeconds(double val) {
            durationInSeconds = val;
            return this;
        }

        /**
         * Sets the {@code controlRateInSeconds} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code controlRateInSeconds} to set
         * @return a reference to this Builder
         */
        public Builder controlRateInSeconds(double val) {
            controlRateInSeconds = val;
            return this;
        }

        /**
         * Returns a {@code FollowTrajectory} built from the parameters previously set.
         *
         * @return a {@code FollowTrajectory} built with parameters of this {@code FollowTrajectory.Builder}
         */
        public FollowTrajectory build() {return new FollowTrajectory(this);}
    }
}
