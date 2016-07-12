package commands;

import control.PidController4d;
import control.PidParameters;
import control.Trajectory4d;
import control.dto.DroneStateStamped;
import control.dto.InertialFrameVelocity;
import services.Velocity4dService;

/**
 * @author Hoang Tung Dinh
 */
public class Velocity4dController implements VelocityController {

    private final Velocity4dService velocity4dService;
    private final PidController4d pidController4d;

    private Velocity4dController(Velocity4dService velocity4dService, PidController4d pidController4d) {
        this.velocity4dService = velocity4dService;
        this.pidController4d = pidController4d;
    }

    public static Velocity4dServiceStep builder() {
        return new Builder();
    }

    @Override
    public void computeAndSendVelocity(double currentTimeInSeconds, DroneStateStamped currentState) {
        final InertialFrameVelocity nextVelocity = pidController4d.compute(currentState.pose(),
                currentState.inertialFrameVelocity(), currentTimeInSeconds);
        velocity4dService.sendVelocity4dMessage(nextVelocity, currentState.pose());
    }

    interface BuildStep {
        Velocity4dController build();
    }

    interface PidAngularZStep {
        BuildStep withPidAngularZ(PidParameters val);
    }

    interface PidLinearZStep {
        PidAngularZStep withPidLinearZ(PidParameters val);
    }

    interface PidLinearYStep {
        PidLinearZStep withPidLinearY(PidParameters val);
    }

    interface PidLinearXStep {
        PidLinearYStep withPidLinearX(PidParameters val);
    }

    interface Trajectory4dStep {
        PidLinearXStep withTrajectory4d(Trajectory4d val);
    }

    interface Velocity4dServiceStep {
        Trajectory4dStep withVelocity4dService(Velocity4dService val);
    }

    /**
     * {@code Velocity4dController} builder static inner class.
     */
    public static final class Builder implements PidAngularZStep, PidLinearZStep, PidLinearYStep, PidLinearXStep,
            Trajectory4dStep, Velocity4dServiceStep, BuildStep {

        private PidParameters pidAngularZ;
        private PidParameters pidLinearZ;
        private PidParameters pidLinearY;
        private PidParameters pidLinearX;
        private Trajectory4d trajectory4d;
        private Velocity4dService velocity4dService;

        private Builder() {}

        /**
         * Sets the {@code pidAngularZ} and returns a reference to {@code BuildStep}
         *
         * @param val the {@code pidAngularZ} to set
         * @return a reference to this AbstractFollowTrajectoryBuilder
         */
        @Override
        public BuildStep withPidAngularZ(PidParameters val) {
            pidAngularZ = val;
            return this;
        }

        /**
         * Sets the {@code pidLinearZ} and returns a reference to {@code PidAngularZStep}
         *
         * @param val the {@code pidLinearZ} to set
         * @return a reference to this AbstractFollowTrajectoryBuilder
         */
        @Override
        public PidAngularZStep withPidLinearZ(PidParameters val) {
            pidLinearZ = val;
            return this;
        }

        /**
         * Sets the {@code pidLinearY} and returns a reference to {@code PidLinearZStep}
         *
         * @param val the {@code pidLinearY} to set
         * @return a reference to this AbstractFollowTrajectoryBuilder
         */
        @Override
        public PidLinearZStep withPidLinearY(PidParameters val) {
            pidLinearY = val;
            return this;
        }

        /**
         * Sets the {@code pidLinearX} and returns a reference to {@code PidLinearYStep}
         *
         * @param val the {@code pidLinearX} to set
         * @return a reference to this AbstractFollowTrajectoryBuilder
         */
        @Override
        public PidLinearYStep withPidLinearX(PidParameters val) {
            pidLinearX = val;
            return this;
        }

        /**
         * Sets the {@code trajectory4d} and returns a reference to {@code PidLinearXStep}
         *
         * @param val the {@code trajectory4d} to set
         * @return a reference to this AbstractFollowTrajectoryBuilder
         */
        @Override
        public PidLinearXStep withTrajectory4d(Trajectory4d val) {
            trajectory4d = val;
            return this;
        }

        /**
         * Sets the {@code velocity4dService} and returns a reference to {@code Trajectory4dStep}
         *
         * @param val the {@code velocity4dService} to set
         * @return a reference to this AbstractFollowTrajectoryBuilder
         */
        @Override
        public Trajectory4dStep withVelocity4dService(Velocity4dService val) {
            velocity4dService = val;
            return this;
        }

        /**
         * Returns a {@code Velocity4dController} built from the parameters previously set.
         *
         * @return a {@code Velocity4dController} built with parameters of this {@code Velocity4dController.AbstractFollowTrajectoryBuilder}
         */
        public Velocity4dController build() {
            final PidController4d pidController4d = PidController4d.builder()
                    .linearXParameters(pidLinearX)
                    .linearYParameters(pidLinearY)
                    .linearZParameters(pidLinearZ)
                    .angularZParameters(pidAngularZ)
                    .trajectory4d(trajectory4d)
                    .build();
            return new Velocity4dController(velocity4dService, pidController4d);
        }
    }
}
