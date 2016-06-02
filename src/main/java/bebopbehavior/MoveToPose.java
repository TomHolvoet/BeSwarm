package bebopbehavior;

/**
 * @author Hoang Tung Dinh
 */

import com.google.common.base.Optional;
import comm.VelocityPublisher;
import control.PidController4D;
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
    private final PidController4D pidController4D;
    private final double durationInSeconds;
    private final double controlRateInSeconds;

    private static final double DEFAULT_CONTROL_RATE_IN_SECONDS = 0.05;

    private MoveToPose(Builder builder) {
        velocityPublisher = builder.velocityPublisher;
        poseProvider = builder.poseProvider;
        velocityProvider = builder.velocityProvider;
        pidController4D = builder.pidController4D;
        durationInSeconds = builder.durationInSeconds;
        controlRateInSeconds = builder.controlRateInSeconds;
    }

    /**
     * {@link Builder#controlRateInSeconds(double)} is optional. All other parameters are mandatory.
     *
     * @return a builder
     */
    public static Builder builder() {
        return new Builder().controlRateInSeconds(DEFAULT_CONTROL_RATE_IN_SECONDS);
    }

    @Override
    public void execute() {
        final Runnable computeNextResponse = new ComputeNextResponse();
        PeriodicTaskRunner.run(computeNextResponse, controlRateInSeconds, durationInSeconds);
    }

    private final class ComputeNextResponse implements Runnable {
        private ComputeNextResponse() {}

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
        private PidController4D pidController4D;
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
         * Sets the {@code pidController4D} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code pidController4D} to set
         * @return a reference to this Builder
         */
        public Builder pidController4D(PidController4D val) {
            pidController4D = val;
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
