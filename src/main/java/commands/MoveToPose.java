package commands;

import control.DefaultPidParameters;
import control.PidParameters;
import control.PoseEstimator;
import control.SinglePointTrajectory4d;
import control.Trajectory4d;
import control.VelocityEstimator;
import control.dto.Pose;
import control.dto.Velocity;
import services.VelocityService;

/**
 * Command for moving to a predefined pose. It is a facade which uses {@link Move}.
 *
 * @author Hoang Tung Dinh
 */
public final class MoveToPose implements Command {
    private final Command followTrajectory;
    private static final double DEFAULT_CONTROL_RATE_IN_SECONDS = 0.05;

    private MoveToPose(Builder builder) {
        final Trajectory4d trajectory4d = SinglePointTrajectory4d.create(builder.goalPose, builder.goalVelocity);
        followTrajectory = FollowTrajectory.builder()
                .velocityPublisher(builder.velocityService)
                .poseEstimator(builder.poseEstimator)
                .velocityEstimator(builder.velocityEstimator)
                .pidLinearParameters(builder.pidLinearParameters)
                .pidAngularParameters(builder.pidAngularParameters)
                .trajectory4d(trajectory4d)
                .durationInSeconds(builder.durationInSeconds)
                .controlRateInSeconds(builder.controlRateInSeconds)
                .build();
    }

    @Override
    public void execute() {
    	followTrajectory.execute();
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


    /**
     * {@code MoveToPose} builder static inner class.
     */
    public static final class Builder {
        private VelocityService velocityService;
        private PoseEstimator poseEstimator;
        private VelocityEstimator velocityEstimator;
        private PidParameters pidLinearParameters;
        private PidParameters pidAngularParameters;
        private Pose goalPose;
        private Velocity goalVelocity;
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
