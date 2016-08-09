package commands;

import control.PidParameters;
import control.Trajectory4d;
import services.Velocity2dService;
import services.Velocity3dService;
import services.Velocity4dService;
import services.VelocityService;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Hoang Tung Dinh
 */
public final class CreateVelocityControllerVisitor {

    private final PidParameters pidLinearXParameters;
    private final PidParameters pidLinearYParameters;
    private final PidParameters pidLinearZParameters;
    private final PidParameters pidAngularZParameters;
    private final Trajectory4d trajectory4d;

    private CreateVelocityControllerVisitor(Builder builder) {
        pidLinearXParameters = builder.pidLinearXParameters;
        pidLinearYParameters = builder.pidLinearYParameters;
        pidLinearZParameters = builder.pidLinearZParameters;
        pidAngularZParameters = builder.pidAngularZParameters;
        trajectory4d = builder.trajectory4d;
    }

    /**
     * Gets a builder for this class.
     *
     * @return a builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a velocity controller using the {@code velocityService}
     *
     * @param velocityService the velocity service used to create the velocity controller
     * @return a velocity controller
     */
    public VelocityController createVelocityController(VelocityService velocityService) {
        if (velocityService instanceof Velocity2dService) {
            return create2dVelocityController((Velocity2dService) velocityService);
        } else if (velocityService instanceof Velocity3dService) {
            return create3dVelocityController((Velocity3dService) velocityService);
        } else if (velocityService instanceof Velocity4dService) {
            return create4dVelocityController((Velocity4dService) velocityService);
        } else {
            throw new IllegalArgumentException("Service type not found." + velocityService);
        }
    }

    private VelocityController create2dVelocityController(Velocity2dService velocity2dService) {
        return Velocity2dController.builder()
                .withTrajectory4d(trajectory4d)
                .withVelocity2dService(velocity2dService)
                .withPidLinearX(pidLinearXParameters)
                .withPidLinearY(pidLinearYParameters)
                .build();
    }

    private VelocityController create3dVelocityController(Velocity3dService velocity3dService) {
        return Velocity3dController.builder()
                .withTrajectory4d(trajectory4d)
                .withVelocity3dService(velocity3dService)
                .withPidLinearX(pidLinearXParameters)
                .withPidLinearY(pidLinearYParameters)
                .withPidLinearZ(pidLinearZParameters)
                .build();
    }

    private VelocityController create4dVelocityController(Velocity4dService velocity4dService) {
        return Velocity4dController.builder()
                .withTrajectory4d(trajectory4d)
                .withVelocity4dService(velocity4dService)
                .withPidLinearX(pidLinearXParameters)
                .withPidLinearY(pidLinearYParameters)
                .withPidLinearZ(pidLinearZParameters)
                .withPidAngularZ(pidAngularZParameters)
                .build();
    }

    /**
     * {@code CreateVelocityControllerVisitor} builder static inner class.
     */
    public static final class Builder {
        private PidParameters pidLinearXParameters;
        private PidParameters pidLinearYParameters;
        private PidParameters pidLinearZParameters;
        private PidParameters pidAngularZParameters;
        private Trajectory4d trajectory4d;

        private Builder() {}

        /**
         * Sets the {@code pidLinearXParameters} and returns a reference to this Builder so that
         * the methods can be chained together.
         *
         * @param val the {@code pidLinearXParameters} to set
         * @return a reference to this Builder
         */
        public Builder withPidLinearXParameters(PidParameters val) {
            pidLinearXParameters = val;
            return this;
        }

        /**
         * Sets the {@code pidLinearYParameters} and returns a reference to this Builder so that
         * the methods can be chained together.
         *
         * @param val the {@code pidLinearYParameters} to set
         * @return a reference to this Builder
         */
        public Builder withPidLinearYParameters(PidParameters val) {
            pidLinearYParameters = val;
            return this;
        }

        /**
         * Sets the {@code pidLinearZParameters} and returns a reference to this Builder so that
         * the methods can be chained together.
         *
         * @param val the {@code pidLinearZParameters} to set
         * @return a reference to this Builder
         */
        public Builder withPidLinearZParameters(PidParameters val) {
            pidLinearZParameters = val;
            return this;
        }

        /**
         * Sets the {@code pidAngularZParameters} and returns a reference to this Builder so that
         * the methods can be chained together.
         *
         * @param val the {@code pidAngularZParameters} to set
         * @return a reference to this Builder
         */
        public Builder withPidAngularZParameters(PidParameters val) {
            pidAngularZParameters = val;
            return this;
        }

        /**
         * Sets the {@code trajectory4d} and returns a reference to this Builder so that the
         * methods can be chained together.
         *
         * @param val the {@code trajectory4d} to set
         * @return a reference to this Builder
         */
        public Builder withTrajectory4d(Trajectory4d val) {
            trajectory4d = val;
            return this;
        }

        /**
         * Returns a {@code CreateVelocityControllerVisitor} built from the parameters previously
         * set.
         *
         * @return a {@code CreateVelocityControllerVisitor} built with parameters of this
         *     {@code CreateVelocityControllerVisitor.Builder}
         */
        public CreateVelocityControllerVisitor build() {
            checkNotNull(pidLinearXParameters);
            checkNotNull(pidLinearYParameters);
            checkNotNull(pidLinearZParameters);
            checkNotNull(pidAngularZParameters);
            checkNotNull(trajectory4d);
            return new CreateVelocityControllerVisitor(this);
        }
    }
}
