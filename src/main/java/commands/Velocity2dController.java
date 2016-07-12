package commands;

import control.PidController1d;
import control.PidParameters;
import control.Trajectory1d;
import control.Trajectory4d;
import control.dto.DroneStateStamped;
import services.Velocity2dService;

/**
 * @author Hoang Tung Dinh
 */
public final class Velocity2dController implements VelocityController {

    private final Velocity2dService velocity2dService;
    private final Trajectory4d trajectory4d;
    private final PidController1d pidControllerLinearX;
    private final PidController1d pidControllerLinearY;

    private Velocity2dController(Builder builder) {
        velocity2dService = builder.velocity2dService;
        trajectory4d = builder.trajectory4d;
        pidControllerLinearX = builder.pidControllerLinearX;
        pidControllerLinearY = builder.pidControllerLinearY;
    }

    public static Velocity2dServiceStep builder() {
        return new Builder();
    }

    @Override
    public void computeAndSendVelocity(double currentTimeInSeconds, DroneStateStamped currentState) {
        final double nextVelocityX = pidControllerLinearX.compute(currentState.pose().x(),
                currentState.inertialFrameVelocity().linearX(), currentTimeInSeconds);
        final double nextVelocityY = pidControllerLinearY.compute(currentState.pose().y(),
                currentState.inertialFrameVelocity().linearY(), currentTimeInSeconds);

        velocity2dService.sendVelocityHeightMessage(nextVelocityX, nextVelocityY,
                trajectory4d.getDesiredPositionZ(currentTimeInSeconds),
                trajectory4d.getDesiredAngleZ(currentTimeInSeconds));
    }

    interface BuildStep {
        Velocity2dController build();
    }

    interface PidLinearYStep {
        BuildStep withPidLinearY(PidParameters val);
    }

    interface PidLinearXStep {
        PidLinearYStep withPidLinearX(PidParameters val);
    }

    interface Trajectory4dStep {
        PidLinearXStep withTrajectory4d(Trajectory4d val);
    }

    interface Velocity2dServiceStep {
        Trajectory4dStep withVelocity2dService(Velocity2dService val);
    }

    /**
     * {@code Velocity2dController} builder static inner class.
     */
    public static final class Builder implements PidLinearYStep, PidLinearXStep, Trajectory4dStep,
            Velocity2dServiceStep, BuildStep {
        private PidController1d pidControllerLinearY;
        private PidController1d pidControllerLinearX;
        private Trajectory4d trajectory4d;
        private Velocity2dService velocity2dService;

        private Builder() {}

        /**
         * Sets the {@code pidControllerLinearY} and returns a reference to {@code BuildStep}
         *
         * @param val the {@code pidLinearY} to set
         * @return a reference to this AbstractFollowTrajectoryBuilder
         */
        @Override
        public BuildStep withPidLinearY(PidParameters val) {
            pidControllerLinearY = PidController1d.create(val, new Trajectory1d() {
                @Override
                public double getDesiredPosition(double timeInSeconds) {
                    return trajectory4d.getDesiredPositionY(timeInSeconds);
                }

                @Override
                public double getDesiredVelocity(double timeInSeconds) {
                    return trajectory4d.getDesiredVelocityY(timeInSeconds);
                }
            });
            return this;
        }

        /**
         * Sets the {@code pidControllerLinearX} and returns a reference to {@code PidLinearYStep}
         *
         * @param val the {@code pidLinearX} to set
         * @return a reference to this AbstractFollowTrajectoryBuilder
         */
        @Override
        public PidLinearYStep withPidLinearX(PidParameters val) {
            pidControllerLinearX = PidController1d.create(val, new Trajectory1d() {
                @Override
                public double getDesiredPosition(double timeInSeconds) {
                    return trajectory4d.getDesiredPositionX(timeInSeconds);
                }

                @Override
                public double getDesiredVelocity(double timeInSeconds) {
                    return trajectory4d.getDesiredVelocityX(timeInSeconds);
                }
            });
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
         * Sets the {@code velocity2dService} and returns a reference to {@code Trajectory4dStep}
         *
         * @param val the {@code velocity2dService} to set
         * @return a reference to this AbstractFollowTrajectoryBuilder
         */
        @Override
        public Trajectory4dStep withVelocity2dService(Velocity2dService val) {
            velocity2dService = val;
            return this;
        }

        /**
         * Returns a {@code Velocity2dController} built from the parameters previously set.
         *
         * @return a {@code Velocity2dController} built with parameters of this {@code Velocity2dController.AbstractFollowTrajectoryBuilder}
         */
        public Velocity2dController build() {return new Velocity2dController(this);}
    }
}
