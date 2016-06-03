package control;

import commands.Pose;
import commands.Velocity;
import geom.EulerAngle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A four-dimensional PID controller for the drone. It is the composition of 4 one-dimensional PID controller
 * {@link PidController1D} (three controllers for the three linear velocities, one controller for the angular velocity).
 *
 * @author Hoang Tung Dinh
 */
public final class PidController4D {

    private static final Logger LOGGER = LoggerFactory.getLogger(PidController4D.class);

    private final PidController1D pidLinearX;
    private final PidController1D pidLinearY;
    private final PidController1D pidLinearZ;
    private final PidController1D pidAngularZ;
    private final Pose goalPose;

    private PidController4D(Builder builder) {
        goalPose = builder.goalPose;
        final Velocity goalVelocity = builder.goalVelocity;

        pidLinearX = PidController1D.builder()
                .goalPoint(goalPose.x())
                .goalVelocity(goalVelocity.linearX())
                .parameters(builder.linearXParameters)
                .build();

        pidLinearY = PidController1D.builder()
                .goalPoint(goalPose.y())
                .goalVelocity(goalVelocity.linearY())
                .parameters(builder.linearYParameters)
                .build();

        pidLinearZ = PidController1D.builder()
                .goalPoint(goalPose.z())
                .goalVelocity(goalVelocity.linearZ())
                .parameters(builder.linearZParameters)
                .build();

        pidAngularZ = PidController1D.builder()
                .goalPoint(goalPose.yaw())
                .goalVelocity(goalVelocity.angularZ())
                .parameters(builder.angularZParameters)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Compute the next velocity (response) of the control loop.
     *
     * @param currentPose     the current pose of the drone
     * @param currentVelocity the current velocity of the drone
     * @return the next velocity (response) of the drone
     */
    public Velocity compute(Pose currentPose, Velocity currentVelocity) {
        final double linearX = pidLinearX.compute(currentPose.x(), currentVelocity.linearX());
        final double linearY = pidLinearY.compute(currentPose.y(), currentVelocity.linearY());
        final double linearZ = pidLinearZ.compute(currentPose.z(), currentVelocity.linearZ());

        final double angularError = EulerAngle.computeAngleDistance(currentPose.yaw(), goalPose.yaw());
        final double adaptedCurrentYaw = goalPose.yaw() - angularError;
        final double angularZ = pidAngularZ.compute(adaptedCurrentYaw, currentVelocity.angularZ());

        LOGGER.debug("Current pose: {} \nCurrent velocity: {}, Current angular error: {}", currentPose, currentVelocity,
                angularError);

        return Velocity.builder().linearX(linearX).linearY(linearY).linearZ(linearZ).angularZ(angularZ).build();
    }

    /**
     * {@code PidController4D} builder static inner class.
     */
    public static final class Builder {
        private Pose goalPose;
        private Velocity goalVelocity;
        private PidParameters linearXParameters;
        private PidParameters linearYParameters;
        private PidParameters linearZParameters;
        private PidParameters angularZParameters;

        private Builder() {}

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
         * Sets the {@code linearXParameters} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code linearXParameters} to set
         * @return a reference to this Builder
         */
        public Builder linearXParameters(PidParameters val) {
            linearXParameters = val;
            return this;
        }

        /**
         * Sets the {@code linearYParameters} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code linearYParameters} to set
         * @return a reference to this Builder
         */
        public Builder linearYParameters(PidParameters val) {
            linearYParameters = val;
            return this;
        }

        /**
         * Sets the {@code linearZParameters} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code linearZParameters} to set
         * @return a reference to this Builder
         */
        public Builder linearZParameters(PidParameters val) {
            linearZParameters = val;
            return this;
        }

        /**
         * Sets the {@code angularZParameters} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code angularZParameters} to set
         * @return a reference to this Builder
         */
        public Builder angularZParameters(PidParameters val) {
            angularZParameters = val;
            return this;
        }

        /**
         * Returns a {@code PidController4D} built from the parameters previously set.
         *
         * @return a {@code PidController4D} built with parameters of this {@code PidController4D.Builder}
         */
        public PidController4D build() {
            return new PidController4D(this);
        }
    }
}
