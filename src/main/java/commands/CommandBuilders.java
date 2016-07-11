package commands;

import control.DefaultPidParameters;
import control.PidParameters;
import control.localization.StateEstimator;
import services.Velocity4dService;

/**
 * @author Hoang Tung Dinh
 * @author Rinde van Lon
 * @author Kristof Coninx
 */
final class CommandBuilders {

    CommandBuilders() {}

    public interface BuildStep<U> {
        BuildStep<U> withPidLinearXParameters(PidParameters val);

        BuildStep<U> withPidLinearYParameters(PidParameters val);

        BuildStep<U> withPidLinearZParameters(PidParameters val);

        BuildStep<U> withPidAngularZParameters(PidParameters val);

        BuildStep<U> withControlRateInSeconds(double val);

        BuildStep<U> withDroneStateLifeDurationInSeconds(double val);

        U build();
    }

    public interface StateEstimatorStep<T> {
        T withStateEstimator(StateEstimator val);
    }

    public interface VelocityServiceStep<T> {
        StateEstimatorStep<T> withVelocityService(Velocity4dService val);
    }

    public interface CopyBuilderStep<T> {
        <N extends AbstractFollowTrajectoryBuilder<?, ?>> T copyOf(N otherBuilder);
    }

    abstract static class AbstractFollowTrajectoryBuilder<T, U> implements StateEstimatorStep<T>,
            VelocityServiceStep<T>, BuildStep<U>, CopyBuilderStep<T> {
        private PidParameters pidLinearXParameters;
        private PidParameters pidLinearYParameters;
        private PidParameters pidLinearZParameters;
        private PidParameters pidAngularZParameters;
        private double controlRateInSeconds;
        private double droneStateLifeDurationInSeconds;
        private StateEstimator stateEstimator;
        private Velocity4dService velocity4dService;

        private static final double DEFAULT_CONTROL_RATE_IN_SECONDS = 0.05;
        private static final double DEFAULT_DRONE_STATE_LIFE_DURATION_IN_SECONDS = 0.1;

        AbstractFollowTrajectoryBuilder() {
            pidLinearXParameters = DefaultPidParameters.LINEAR_X.getParameters();
            pidLinearYParameters = DefaultPidParameters.LINEAR_Y.getParameters();
            pidLinearZParameters = DefaultPidParameters.LINEAR_Z.getParameters();
            pidAngularZParameters = DefaultPidParameters.ANGULAR_Z.getParameters();
            controlRateInSeconds = DEFAULT_CONTROL_RATE_IN_SECONDS;
            droneStateLifeDurationInSeconds = DEFAULT_DRONE_STATE_LIFE_DURATION_IN_SECONDS;
        }

        abstract T nextInterfaceInBuilderChain();

        @Override
        public T withStateEstimator(StateEstimator val) {
            stateEstimator = val;
            return nextInterfaceInBuilderChain();
        }

        @Override
        public StateEstimatorStep<T> withVelocityService(Velocity4dService val) {
            velocity4dService = val;
            return this;
        }

        @Override
        public BuildStep<U> withPidLinearXParameters(PidParameters val) {
            pidLinearXParameters = val;
            return this;
        }

        @Override
        public BuildStep<U> withPidLinearYParameters(PidParameters val) {
            pidLinearYParameters = val;
            return this;
        }

        @Override
        public BuildStep<U> withPidLinearZParameters(PidParameters val) {
            pidLinearZParameters = val;
            return this;
        }

        @Override
        public BuildStep<U> withPidAngularZParameters(PidParameters val) {
            pidAngularZParameters = val;
            return this;
        }

        @Override
        public BuildStep<U> withControlRateInSeconds(double val) {
            controlRateInSeconds = val;
            return this;
        }

        @Override
        public BuildStep<U> withDroneStateLifeDurationInSeconds(double val) {
            droneStateLifeDurationInSeconds = val;
            return this;
        }

        PidParameters getPidLinearXParameters() {
            return pidLinearXParameters;
        }

        PidParameters getPidLinearYParameters() {
            return pidLinearYParameters;
        }

        PidParameters getPidLinearZParameters() {
            return pidLinearZParameters;
        }

        PidParameters getPidAngularZParameters() {
            return pidAngularZParameters;
        }

        double getControlRateInSeconds() {
            return controlRateInSeconds;
        }

        double getDroneStateLifeDurationInSeconds() {
            return droneStateLifeDurationInSeconds;
        }

        StateEstimator getStateEstimator() {
            return stateEstimator;
        }

        Velocity4dService getVelocity4dService() {
            return velocity4dService;
        }

        @Override
        public <N extends AbstractFollowTrajectoryBuilder<?, ?>> T copyOf(N otherBuilder) {
            pidLinearXParameters = otherBuilder.getPidLinearXParameters();
            pidLinearYParameters = otherBuilder.getPidLinearYParameters();
            pidLinearZParameters = otherBuilder.getPidLinearZParameters();
            pidAngularZParameters = otherBuilder.getPidAngularZParameters();
            controlRateInSeconds = otherBuilder.getControlRateInSeconds();
            droneStateLifeDurationInSeconds = otherBuilder.getDroneStateLifeDurationInSeconds();
            stateEstimator = otherBuilder.getStateEstimator();
            velocity4dService = otherBuilder.getVelocity4dService();

            return nextInterfaceInBuilderChain();
        }
    }
}
