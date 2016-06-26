package control;

import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.math.EulerAngle;

/**
 * A four-dimensional PID controller for the drone. It is the composition of 4 one-dimensional PID controller
 * {@link PidController1d} (three controllers for the three linear velocities, one controller for the
 * angular velocity).
 *
 * @author Hoang Tung Dinh
 */
public final class PidController4d {

    private static final Logger poseLogger = LoggerFactory.getLogger(PidController4d.class.getName() + ".poselogger");
    private static final Logger velocityLogger = LoggerFactory.getLogger(
            PidController4d.class.getName() + ".velocitylogger");

    private final PidController1d pidLinearX;
    private final PidController1d pidLinearY;
    private final PidController1d pidLinearZ;
    private final PidController1d pidAngularZ;

    private final Trajectory4d trajectory4d;

    private PidController4d(Builder builder) {
        trajectory4d = builder.trajectory4d;
        pidLinearX = PidController1d.create(builder.linearXParameters, trajectory4d.getTrajectoryLinearX());
        pidLinearY = PidController1d.create(builder.linearYParameters, trajectory4d.getTrajectoryLinearY());
        pidLinearZ = PidController1d.create(builder.linearZParameters, trajectory4d.getTrajectoryLinearZ());
        pidAngularZ = PidController1d.create(builder.angularZParameters, trajectory4d.getTrajectoryAngularZ());
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
    public InertialFrameVelocity compute(Pose currentPose, InertialFrameVelocity currentVelocity,
            double currentTimeInSeconds) {
        final double linearX = pidLinearX.compute(currentPose.x(), currentVelocity.linearX(), currentTimeInSeconds);
        final double linearY = pidLinearY.compute(currentPose.y(), currentVelocity.linearY(), currentTimeInSeconds);
        final double linearZ = pidLinearZ.compute(currentPose.z(), currentVelocity.linearZ(), currentTimeInSeconds);

        final double desiredYaw = trajectory4d.getTrajectoryAngularZ().getDesiredPosition(currentTimeInSeconds);
        final double angularError = EulerAngle.computeAngleDistance(currentPose.yaw(), desiredYaw);
        final double adaptedCurrentYaw = desiredYaw - angularError;
        final double angularZ = pidAngularZ.compute(adaptedCurrentYaw, currentVelocity.angularZ(),
                currentTimeInSeconds);

        poseLogger.trace("{} {} {} {} {} {} {} {} {}", currentTimeInSeconds, currentPose.x(), currentPose.y(),
                currentPose.z(), currentPose.yaw(),
                trajectory4d.getTrajectoryLinearX().getDesiredPosition(currentTimeInSeconds),
                trajectory4d.getTrajectoryLinearY().getDesiredPosition(currentTimeInSeconds),
                trajectory4d.getTrajectoryLinearZ().getDesiredPosition(currentTimeInSeconds),
                trajectory4d.getTrajectoryAngularZ().getDesiredPosition(currentTimeInSeconds));

        velocityLogger.trace("{} {} {} {} {} {} {} {} {}", currentTimeInSeconds, currentVelocity.linearX(),
                currentVelocity.linearY(), currentVelocity.linearZ(), currentVelocity.angularZ(),
                trajectory4d.getTrajectoryLinearX().getDesiredVelocity(currentTimeInSeconds),
                trajectory4d.getTrajectoryLinearY().getDesiredVelocity(currentTimeInSeconds),
                trajectory4d.getTrajectoryLinearZ().getDesiredVelocity(currentTimeInSeconds),
                trajectory4d.getTrajectoryAngularZ().getDesiredVelocity(currentTimeInSeconds));

        return Velocity.builder().linearX(linearX).linearY(linearY).linearZ(linearZ).angularZ(angularZ).build();
    }

    /**
     * {@code PidController4d} builder static inner class.
     */
    public static final class Builder {
        private Trajectory4d trajectory4d;
        private PidParameters linearXParameters;
        private PidParameters linearYParameters;
        private PidParameters linearZParameters;
        private PidParameters angularZParameters;

        private Builder() {}

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
         * Returns a {@code PidController4d} built from the parameters previously set.
         *
         * @return a {@code PidController4d} built with parameters of this {@code PidController4d.Builder}
         */
        public PidController4d build() {return new PidController4d(this);}
    }
}
