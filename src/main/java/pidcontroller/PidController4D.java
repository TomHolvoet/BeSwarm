package pidcontroller;

import bebopcontrol.Pose;
import bebopcontrol.Velocity;

/**
 * @author Hoang Tung Dinh
 */
public final class PidController4D {
    private final Pose goalPose;
    private final Velocity goalVelocity;

    private final PidController1D pidLinearX;
    private final PidController1D pidLinearY;
    private final PidController1D pidLinearZ;
    private final PidController1D pidAngularZ;

    private PidController4D(Builder builder) {
        goalPose = builder.goalPose;
        goalVelocity = builder.goalVelocity;

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

    public Velocity compute(Pose currentPose, Velocity currentVelocity) {
        final double linearX = pidLinearX.compute(currentPose.x(), currentVelocity.linearX());
        final double linearY = pidLinearY.compute(currentPose.y(), currentVelocity.linearY());
        final double linearZ = pidLinearZ.compute(currentPose.z(), currentVelocity.linearZ());
        final double angularZ = pidAngularZ.compute(currentPose.yaw(), currentVelocity.angularZ());

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
