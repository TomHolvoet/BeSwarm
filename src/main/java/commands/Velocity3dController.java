package commands;

import applications.trajectory.TrajectoryUtils;
import control.PidController1d;
import control.PidParameters;
import control.Trajectory4d;
import control.dto.DroneStateStamped;
import services.Velocity3dService;

/**
 * @author Hoang Tung Dinh
 */
public class Velocity3dController implements VelocityController {

    private final Velocity3dService velocity3dService;
    private final Trajectory4d trajectory4d;
    private final PidController1d pidControllerLinearX;
    private final PidController1d pidControllerLinearY;
    private final PidController1d pidControllerLinearZ;

    private Velocity3dController(Builder builder) {
        velocity3dService = builder.velocity3dService;
        trajectory4d = builder.trajectory4d;
        pidControllerLinearX = builder.pidControllerLinearX;
        pidControllerLinearY = builder.pidControllerLinearY;
        pidControllerLinearZ = builder.pidControllerLinearZ;
    }

    public static Velocity3dServiceStep builder() {
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

    interface BuildStep {
        Velocity3dController build();
    }

    interface PidLinearZStep {
        BuildStep withPidLinearZ(PidParameters val);
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

    interface Velocity3dServiceStep {
        Trajectory4dStep withVelocity3dService(Velocity3dService val);
    }

    /**
     * {@code Velocity3dController} builder static inner class.
     */
    public static final class Builder implements PidLinearZStep, PidLinearYStep, PidLinearXStep, Trajectory4dStep,
            Velocity3dServiceStep, BuildStep {

        private PidController1d pidControllerLinearZ;
        private PidController1d pidControllerLinearY;
        private PidController1d pidControllerLinearX;
        private Trajectory4d trajectory4d;
        private Velocity3dService velocity3dService;

        private Builder() {}

        /**
         * Sets the {@code pidLinearZ} and returns a reference to {@code BuildStep}
         *
         * @param val the {@code pidLinearZ} to set
         * @return a reference to this Builder
         */
        @Override
        public BuildStep withPidLinearZ(PidParameters val) {
            pidControllerLinearZ = PidController1d.create(val, TrajectoryUtils.getTrajectoryLinearZ(trajectory4d));
            return this;
        }

        /**
         * Sets the {@code pidLinearY} and returns a reference to {@code PidLinearZStep}
         *
         * @param val the {@code pidLinearY} to set
         * @return a reference to this Builder
         */
        @Override
        public PidLinearZStep withPidLinearY(PidParameters val) {
            pidControllerLinearY = PidController1d.create(val, TrajectoryUtils.getTrajectoryLinearY(trajectory4d));
            return this;
        }

        /**
         * Sets the {@code pidLinearX} and returns a reference to {@code PidLinearYStep}
         *
         * @param val the {@code pidLinearX} to set
         * @return a reference to this Builder
         */
        @Override
        public PidLinearYStep withPidLinearX(PidParameters val) {
            pidControllerLinearX = PidController1d.create(val, TrajectoryUtils.getTrajectoryLinearX(trajectory4d));
            return this;
        }

        /**
         * Sets the {@code trajectory4d} and returns a reference to {@code PidLinearXStep}
         *
         * @param val the {@code trajectory4d} to set
         * @return a reference to this Builder
         */
        @Override
        public PidLinearXStep withTrajectory4d(Trajectory4d val) {
            trajectory4d = val;
            return this;
        }

        /**
         * Sets the {@code velocity3dService} and returns a reference to {@code Trajectory4dStep}
         *
         * @param val the {@code velocity3dService} to set
         * @return a reference to this Builder
         */
        @Override
        public Trajectory4dStep withVelocity3dService(Velocity3dService val) {
            velocity3dService = val;
            return this;
        }

        /**
         * Returns a {@code Velocity3dController} built from the parameters previously set.
         *
         * @return a {@code Velocity3dController} built with parameters of this {@code Velocity3dController.Builder}
         */
        public Velocity3dController build() {return new Velocity3dController(this);}
    }
}
