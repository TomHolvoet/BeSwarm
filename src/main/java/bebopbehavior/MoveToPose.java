package bebopbehavior;

/**
 * @author Hoang Tung Dinh
 */

import com.google.common.base.Optional;
import comm.VelocityPublisher;
import control.PidController4D;
import control.PidParameters;
import control.PoseProvider;
import control.VelocityProvider;

/**
 * Command for moving to a predefined pose. It is a facade which uses {@link Move}.
 *
 * @author Hoang Tung Dinh
 */
public final class MoveToPose implements Command {
    private final VelocityPublisher velocityPublisher;
    private final PoseProvider poseProvider;
    private final VelocityProvider velocityProvider;
    private final PidParameters pidLinearParameters;
    private final PidParameters pidAngularParameters;
    private final Pose goalPose;
    private final Velocity goalVelocity;
    private final double durationInSeconds;
    private final double controlRateInSeconds;

    private static final double DEFAULT_CONTROL_RATE_IN_SECONDS = 0.05;
    private static final PidParameters DEFAULT_PID_LINEAR_PARAMETERS = PidParameters.builder()
            .kp(0.5)
            .kd(1)
            .ki(0)
            .build();
    private static final PidParameters DEFAULT_PID_ANGULAR_PARAMETERS = PidParameters.builder()
            .kp(0.1)
            .kd(0.5)
            .ki(0)
            .build();

    private MoveToPose(Builder builder) {
        velocityPublisher = builder.velocityPublisher;
        poseProvider = builder.poseProvider;
        velocityProvider = builder.velocityProvider;
        pidLinearParameters = builder.pidLinearParameters;
        pidAngularParameters = builder.pidAngularParameters;
        goalPose = builder.goalPose;
        goalVelocity = builder.goalVelocity;
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
                .pidLinearParameters(DEFAULT_PID_LINEAR_PARAMETERS)
                .pidAngularParameters(DEFAULT_PID_ANGULAR_PARAMETERS);
    }

    @Override
    public void execute() {
        final PidController4D pidController4D = PidController4D.builder()
                .linearXParameters(pidLinearParameters)
                .linearYParameters(pidLinearParameters)
                .linearZParameters(pidLinearParameters)
                .angularZParameters(pidAngularParameters)
                .goalPose(goalPose)
                .goalVelocity(goalVelocity)
                .build();

        final Runnable computeNextResponse = new ComputeNextResponse(pidController4D);
        PeriodicTaskRunner.run(computeNextResponse, controlRateInSeconds, durationInSeconds);
    }

    private final class ComputeNextResponse implements Runnable {
        private final PidController4D pidController4D;

        private ComputeNextResponse(PidController4D pidController4D) {
            this.pidController4D = pidController4D;
        }

        @Override
        public void run() {
            final Optional<Pose> currentPose = poseProvider.getCurrentPose();
            if (!currentPose.isPresent()) {
                return;
            }

            final Optional<Velocity> currentVelocityInGlobalFrame = velocityProvider.getCurrentVelocity();
            if (!currentVelocityInGlobalFrame.isPresent()) {
                return;
            }

            final Velocity nextVelocityInGlobalFrame = pidController4D.compute(currentPose.get(),
                    currentVelocityInGlobalFrame.get());
            final Velocity nextVelocityInLocalFrame = Velocity.createLocalVelocityFromGlobalVelocity(
                    nextVelocityInGlobalFrame, currentPose.get().yaw());
            velocityPublisher.publishVelocityCommand(nextVelocityInLocalFrame);
        }
    }

    /**
     * {@code MoveToPose} builder static inner class.
     */
    public static final class Builder {
        private VelocityPublisher velocityPublisher;
        private PoseProvider poseProvider;
        private VelocityProvider velocityProvider;
        private PidParameters pidLinearParameters;
        private PidParameters pidAngularParameters;
        private Pose goalPose;
        private Velocity goalVelocity;
        private double durationInSeconds;
        private double controlRateInSeconds;

        private Builder() {}

        /**
         * Sets the {@code velocityPublisher} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code velocityPublisher} to set
         * @return a reference to this Builder
         */
        public Builder velocityPublisher(VelocityPublisher val) {
            velocityPublisher = val;
            return this;
        }

        /**
         * Sets the {@code poseProvider} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code poseProvider} to set
         * @return a reference to this Builder
         */
        public Builder poseProvider(PoseProvider val) {
            poseProvider = val;
            return this;
        }

        /**
         * Sets the {@code velocityProvider} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code velocityProvider} to set
         * @return a reference to this Builder
         */
        public Builder velocityProvider(VelocityProvider val) {
            velocityProvider = val;
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
         * Sets the {@code goalPose} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code goalPose} to set
         * @return a reference to this Builder
         */
        public Builder goalPose(Pose val) {
            goalPose = val;
            return this;
        }

        /**
         * Sets the {@code goalVelocity} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code goalVelocity} to set
         * @return a reference to this Builder
         */
        public Builder goalVelocity(Velocity val) {
            goalVelocity = val;
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
         * Returns a {@code MoveToPose} built from the parameters previously set.
         *
         * @return a {@code MoveToPose} built with parameters of this {@code MoveToPose.Builder}
         */
        public MoveToPose build() {return new MoveToPose(this);}
    }
}
