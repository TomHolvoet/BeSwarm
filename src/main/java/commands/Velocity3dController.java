package commands;

import applications.trajectory.TrajectoryUtils;
import control.DefaultPidParameters;
import control.PidController1d;
import control.PidParameters;
import control.Trajectory4d;
import control.dto.DroneStateStamped;
import services.Velocity3dService;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Hoang Tung Dinh
 */
final class Velocity3dController implements VelocityController {

    private final Velocity3dService velocity3dService;
    private final Trajectory4d trajectory4d;
    private final PidController1d pidControllerLinearX;
    private final PidController1d pidControllerLinearY;
    private final PidController1d pidControllerLinearZ;

    Velocity3dController(Builder builder) {
        velocity3dService = builder.velocity3dService;
        trajectory4d = builder.trajectory4d;
        pidControllerLinearX = PidController1d.create(builder.pidLinearX,
                TrajectoryUtils.getTrajectoryLinearX(trajectory4d));
        pidControllerLinearY = PidController1d.create(builder.pidLinearY,
                TrajectoryUtils.getTrajectoryLinearY(trajectory4d));
        pidControllerLinearZ = PidController1d.create(builder.pidLinearZ,
                TrajectoryUtils.getTrajectoryLinearZ(trajectory4d));
    }

    /**
     * Gets a builder of this class.
     *
     * @return a builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void computeAndSendVelocity(double currentTimeInSeconds, DroneStateStamped currentState) {
        final double nextVelocityX = pidControllerLinearX.compute(currentState.pose().x(),
                currentState.inertialFrameVelocity().linearX(), currentTimeInSeconds);
        final double nextVelocityY = pidControllerLinearY.compute(currentState.pose().y(),
                currentState.inertialFrameVelocity().linearY(), currentTimeInSeconds);
        final double nextVelocityZ = pidControllerLinearZ.compute(currentState.pose().z(),
                currentState.inertialFrameVelocity().linearZ(), currentTimeInSeconds);

        velocity3dService.sendVelocity3dMessage(nextVelocityX, nextVelocityY, nextVelocityZ,
                trajectory4d.getDesiredAngleZ(currentTimeInSeconds));
    }

    /**
     * Builds a {@link Velocity3dController} instance.
     */
    public static final class Builder extends BuilderWithVelocity3dService<Builder> {

        private Velocity3dService velocity3dService;

        Builder() {}

        @Override
        Builder self() {
            return this;
        }

        /**
         * Sets the {@code velocity3dService} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code velocity3dService} to set
         * @return a reference to this Builder
         */
        public Builder withVelocity3dService(Velocity3dService val) {
            velocity3dService = val;
            return this;
        }

        /**
         * Returns a {@code Velocity3dController} built from the parameters previously set.
         *
         * @return a {@code Velocity3dController} built with parameters of this {@code Velocity3dController.Builder}
         */
        public Velocity3dController build() {
            checkNotNull(velocity3dService);
            checkNotNull(trajectory4d);
            checkNotNull(pidLinearX);
            checkNotNull(pidLinearY);
            checkNotNull(pidLinearZ);
            return new Velocity3dController(this);
        }
    }

    abstract static class BuilderWithVelocity3dService<T extends BuilderWithVelocity3dService<T>> extends
            Velocity2dController.BuilderWithVelocity2dService<T> {
        PidParameters pidLinearZ;

        BuilderWithVelocity3dService() {
            pidLinearZ = DefaultPidParameters.LINEAR_Z.getParameters();
        }

        /**
         * Sets the {@code pidLinearZ} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code pidLinearZ} to set
         * @return a reference to this Builder
         */
        public final T withPidLinearZ(PidParameters val) {
            pidLinearZ = val;
            return self();
        }
    }
}
