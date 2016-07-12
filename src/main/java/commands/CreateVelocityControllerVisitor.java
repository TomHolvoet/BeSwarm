package commands;

import control.PidParameters;
import control.Trajectory4d;
import services.Velocity2dService;
import services.Velocity3dService;
import services.Velocity4dService;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Hoang Tung Dinh
 */
public class CreateVelocityControllerVisitor implements VelocityServiceVisitor {

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

    public static Builder builder() {return new Builder();}

    @Override
    public VelocityController visit(Velocity2dService velocity2dService) {
        return Velocity2dController.builder()
                .withTrajectory4d(trajectory4d)
                .withVelocity2dService(velocity2dService)
                .withPidLinearX(pidLinearXParameters)
                .withPidLinearY(pidLinearYParameters)
                .build();
    }

    @Override
    public VelocityController visit(Velocity3dService velocity3dService) {
        return Velocity3dController.builder()
                .withTrajectory4d(trajectory4d)
                .withVelocity3dService(velocity3dService)
                .withPidLinearX(pidLinearXParameters)
                .withPidLinearY(pidLinearYParameters)
                .withPidLinearZ(pidLinearZParameters)
                .build();
    }

    @Override
    public VelocityController visit(Velocity4dService velocity4dService) {
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
         * Sets the {@code pidLinearXParameters} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code pidLinearXParameters} to set
         * @return a reference to this Builder
         */
        public Builder withPidLinearXParameters(PidParameters val) {
            pidLinearXParameters = val;
            return this;
        }

        /**
         * Sets the {@code pidLinearYParameters} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code pidLinearYParameters} to set
         * @return a reference to this Builder
         */
        public Builder withPidLinearYParameters(PidParameters val) {
            pidLinearYParameters = val;
            return this;
        }

        /**
         * Sets the {@code pidLinearZParameters} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code pidLinearZParameters} to set
         * @return a reference to this Builder
         */
        public Builder withPidLinearZParameters(PidParameters val) {
            pidLinearZParameters = val;
            return this;
        }

        /**
         * Sets the {@code pidAngularZParameters} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code pidAngularZParameters} to set
         * @return a reference to this Builder
         */
        public Builder withPidAngularZParameters(PidParameters val) {
            pidAngularZParameters = val;
            return this;
        }

        /**
         * Sets the {@code trajectory4d} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code trajectory4d} to set
         * @return a reference to this Builder
         */
        public Builder withTrajectory4d(Trajectory4d val) {
            trajectory4d = val;
            return this;
        }

        /**
         * Returns a {@code CreateVelocityControllerVisitor} built from the parameters previously set.
         *
         * @return a {@code CreateVelocityControllerVisitor} built with parameters of this {@code
         * CreateVelocityControllerVisitor.Builder}
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
