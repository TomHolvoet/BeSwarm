package commands;

import control.DefaultPidParameters;
import control.PidController4d;
import control.PidParameters;
import control.dto.DroneStateStamped;
import control.dto.InertialFrameVelocity;
import services.Velocity4dService;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Hoang Tung Dinh
 */
final class Velocity4dController implements VelocityController {

    private final Velocity4dService velocity4dService;
    private final PidController4d pidController4d;

    Velocity4dController(Velocity4dService velocity4dService, PidController4d pidController4d) {
        this.velocity4dService = velocity4dService;
        this.pidController4d = pidController4d;
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
        final InertialFrameVelocity nextVelocity = pidController4d.compute(currentState.pose(),
                currentState.inertialFrameVelocity(), currentTimeInSeconds);
        velocity4dService.sendVelocity4dMessage(nextVelocity, currentState.pose());
    }

    /**
     * Builds a {@link Velocity4dController} instance.
     */
    public static final class Builder extends BuilderWithVelocity4dService<Builder> {

        private Velocity4dService velocity4dService;

        Builder() {}

        @Override
        Builder self() {
            return this;
        }

        /**
         * Sets the {@code velocity4dService} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code velocity4dService} to set
         * @return a reference to this Builder
         */
        public Builder withVelocity4dService(Velocity4dService val) {
            velocity4dService = val;
            return this;
        }

        /**
         * Returns a {@code Velocity4dController} built from the parameters previously set.
         *
         * @return a {@code Velocity4dController} built with parameters of this {@code Velocity4dController.Builder}
         */
        public Velocity4dController build() {
            checkNotNull(velocity4dService);
            checkNotNull(trajectory4d);
            checkNotNull(pidLinearX);
            checkNotNull(pidLinearY);
            checkNotNull(pidLinearZ);

            final PidController4d pidController4d = PidController4d.builder()
                    .trajectory4d(trajectory4d)
                    .linearXParameters(pidLinearX)
                    .linearYParameters(pidLinearY)
                    .linearZParameters(pidLinearZ)
                    .angularZParameters(pidAngularZ)
                    .build();

            return new Velocity4dController(velocity4dService, pidController4d);
        }
    }

    abstract static class BuilderWithVelocity4dService<T extends BuilderWithVelocity4dService<T>> extends
            Velocity3dController.BuilderWithVelocity3dService<T> {
        PidParameters pidAngularZ;

        BuilderWithVelocity4dService() {
            pidAngularZ = DefaultPidParameters.ANGULAR_Z.getParameters();
        }

        /**
         * Sets the {@code pidAngularZ} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code pidAngularZ} to set
         * @return a reference to this Builder
         */
        public final T withPidAngularZ(PidParameters val) {
            pidAngularZ = val;
            return self();
        }
    }
}
